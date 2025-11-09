import { ElNotification } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import { StorageConfig } from '@/utils/storage/storage-config'
import { fetchLatestChangeLog } from '@/api/change-log'

/**
 * 版本管理器
 * 负责处理版本比较、升级检测和数据清理
 */
class VersionManager {
  private latestLog: Api.ChangeLog.Item | null = null

  /**
   * 规范化版本号字符串，移除前缀 'v'
   */
  private normalizeVersion(version: string): string {
    return version.replace(/^v/, '')
  }

  /**
   * 获取存储的版本号
   */
  private getStoredVersion(): string | null {
    return localStorage.getItem(StorageConfig.VERSION_KEY)
  }

  /**
   * 设置版本号到存储
   */
  private setStoredVersion(version: string): void {
    localStorage.setItem(StorageConfig.VERSION_KEY, version)
  }

  /**
   * 获取最新的更新日志（仅获取一次）
   */
  private async ensureLatestLog(): Promise<Api.ChangeLog.Item | null> {
    if (this.latestLog) {
      return this.latestLog
    }
    try {
      const latest = await fetchLatestChangeLog()
      this.latestLog = latest ?? null
    } catch (error) {
      console.warn('[Upgrade] 获取最新更新日志失败', error)
      this.latestLog = null
    }
    return this.latestLog
  }

  /**
   * 检查是否应该跳过升级处理
   */
  private shouldSkipUpgrade(): boolean {
    return StorageConfig.CURRENT_VERSION === StorageConfig.SKIP_UPGRADE_VERSION
  }

  /**
   * 检查是否为首次访问
   */
  private isFirstVisit(storedVersion: string | null): boolean {
    return !storedVersion
  }

  /**
   * 检查版本是否相同
   */
  private isSameVersion(storedVersion: string): boolean {
    return storedVersion === StorageConfig.CURRENT_VERSION
  }

  /**
   * 查找旧的存储结构
   */
  private findLegacyStorage(): { oldSysKey: string | null; oldVersionKeys: string[] } {
    const storageKeys = Object.keys(localStorage)
    const currentVersionPrefix = StorageConfig.generateStorageKey('').slice(0, -1) // 移除末尾的 '-'

    // 查找旧的单一存储结构
    const oldSysKey =
      storageKeys.find(
        (key) =>
          StorageConfig.isVersionedKey(key) && key !== currentVersionPrefix && !key.includes('-')
      ) || null

    // 查找旧版本的分离存储键
    const oldVersionKeys = storageKeys.filter(
      (key) =>
        StorageConfig.isVersionedKey(key) &&
        !StorageConfig.isCurrentVersionKey(key) &&
        key.includes('-')
    )

    return { oldSysKey, oldVersionKeys }
  }

  /**
   * 构建升级通知消息
   */
  private buildUpgradeMessage(latestLog: Api.ChangeLog.Item | null): string {
    const version = latestLog?.version || StorageConfig.CURRENT_VERSION
    const summary = latestLog?.summary || '本次更新带来了体验与性能的提升。'
    const content = latestLog?.content
    const parts = [
      `<p style="color: var(--art-gray-text-800); padding-bottom: 6px;">系统已升级到 ${version} 版本：</p>`,
      `<p style="font-weight:600; margin-bottom: 8px;">${summary}</p>`
    ]
    if (content) {
      parts.push(`<div style="line-height:1.6;">${content}</div>`)
    }
    parts.push(`<p style="margin-top: 10px;">查看更新日志了解更多详情。</p>`)
    return parts.join('')
  }

  /**
   * 显示升级通知
   */
  private showUpgradeNotification(message: string): void {
    ElNotification({
      title: '系统升级公告',
      message,
      duration: 0,
      type: 'success',
      dangerouslyUseHTMLString: true
    })
  }

  /**
   * 清理旧版本数据
   */
  private cleanupLegacyData(oldSysKey: string | null, oldVersionKeys: string[]): void {
    // 清理旧的单一存储结构
    if (oldSysKey) {
      localStorage.removeItem(oldSysKey)
      console.info(`[Upgrade] 已清理旧存储: ${oldSysKey}`)
    }

    // 清理旧版本的分离存储
    oldVersionKeys.forEach((key) => {
      localStorage.removeItem(key)
      console.info(`[Upgrade] 已清理旧存储: ${key}`)
    })
  }

  /**
   * 执行升级流程
   */
  private async executeUpgrade(
    storedVersion: string,
    legacyStorage: ReturnType<typeof this.findLegacyStorage>
  ): Promise<void> {
    try {
      const latestLog = await this.ensureLatestLog()
      const message = this.buildUpgradeMessage(latestLog)

      // 显示升级通知
      this.showUpgradeNotification(message)

      // 更新版本号
      this.setStoredVersion(StorageConfig.CURRENT_VERSION)

      // 清理旧数据
      this.cleanupLegacyData(legacyStorage.oldSysKey, legacyStorage.oldVersionKeys)

      console.info(`[Upgrade] 升级完成: ${storedVersion} → ${StorageConfig.CURRENT_VERSION}`)
    } catch (error) {
      console.error('[Upgrade] 系统升级处理失败:', error)
    }
  }

  /**
   * 系统升级处理主流程
   */
  async processUpgrade(): Promise<void> {
    // 跳过特定版本
    if (this.shouldSkipUpgrade()) {
      console.debug('[Upgrade] 跳过版本升级检查')
      return
    }

    const storedVersion = this.getStoredVersion()

    // 首次访问处理
    if (this.isFirstVisit(storedVersion)) {
      this.setStoredVersion(StorageConfig.CURRENT_VERSION)
      // console.info('[Upgrade] 首次访问，已设置当前版本')
      return
    }

    // 版本相同，无需升级
    if (this.isSameVersion(storedVersion!)) {
      // console.debug('[Upgrade] 版本相同，无需升级')
      return
    }

    // 检查是否有需要升级的旧数据
    const legacyStorage = this.findLegacyStorage()
    if (!legacyStorage.oldSysKey && legacyStorage.oldVersionKeys.length === 0) {
      this.setStoredVersion(StorageConfig.CURRENT_VERSION)
      console.info('[Upgrade] 无旧数据，已更新版本号')
      return
    }

    // 延迟执行升级流程，确保应用已完全加载
    setTimeout(() => {
      this.executeUpgrade(storedVersion!, legacyStorage)
    }, StorageConfig.UPGRADE_DELAY)
  }
}

// 创建版本管理器实例
const versionManager = new VersionManager()

/**
 * 系统升级处理入口函数
 */
export async function systemUpgrade(): Promise<void> {
  await versionManager.processUpgrade()
}
