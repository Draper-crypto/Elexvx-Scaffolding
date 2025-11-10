<template>
  <div class="plugin-page art-full-height">
    <ElCard class="plugin-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>插件列表</span>
          <ElButton type="primary" text :loading="loading" @click="loadPlugins">
            刷新
          </ElButton>
        </div>
      </template>

      <ElAlert
        class="plugin-alert"
        type="info"
        :closable="false"
        show-icon
        description="将插件包解压到 plugins 目录即可在此页面载入或弹出。载入时系统会自动记录操作日志并创建数据目录。"
      />

      <ElTable :data="plugins" stripe v-loading="loading">
        <ElTableColumn type="index" width="60" label="#" />
        <ElTableColumn prop="name" label="插件名称" min-width="180">
          <template #default="{ row }">
            <div class="plugin-name">
              <span class="plugin-name__text">{{ row.name }}</span>
              <ElTag v-if="!row.available" type="danger" size="small">清单缺失</ElTag>
              <ElTooltip v-else-if="row.manifestError" :content="row.manifestError" placement="top">
                <ElTag type="warning" size="small">清单异常</ElTag>
              </ElTooltip>
            </div>
            <div class="plugin-key">{{ row.pluginKey }}</div>
          </template>
        </ElTableColumn>
        <ElTableColumn prop="description" label="描述" min-width="220" show-overflow-tooltip />
        <ElTableColumn prop="version" label="版本" width="100" />
        <ElTableColumn prop="status" label="状态" width="120">
          <template #default="{ row }">
            <ElTag :type="statusTag(row).type">{{ statusTag(row).text }}</ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn prop="backendEntry" label="后端入口" min-width="160">
          <template #default="{ row }">
            {{ row.backendEntry || '-' }}
          </template>
        </ElTableColumn>
        <ElTableColumn prop="frontendEntry" label="前端入口" min-width="160">
          <template #default="{ row }">
            {{ row.frontendEntry || '-' }}
          </template>
        </ElTableColumn>
        <ElTableColumn prop="databaseScripts" label="数据库脚本" min-width="200">
          <template #default="{ row }">
            <div class="script-list" v-if="row.databaseScripts?.length">
              <span v-for="item in row.databaseScripts" :key="item">{{ item }}</span>
            </div>
            <span v-else>-</span>
          </template>
        </ElTableColumn>
        <ElTableColumn prop="dataPath" label="数据目录" min-width="200">
          <template #default="{ row }">
            {{ row.dataPath || '-' }}
          </template>
        </ElTableColumn>
        <ElTableColumn label="最近变更" width="200">
          <template #default="{ row }">
            <div>载入：{{ row.lastLoadedAt || '—' }}</div>
            <div>弹出：{{ row.lastUnloadedAt || '—' }}</div>
          </template>
        </ElTableColumn>
        <ElTableColumn label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <ElButton
                size="small"
                type="primary"
                :disabled="loading || row.status === 'ENABLED' || !row.available"
                @click="handleLoad(row)"
              >
                载入
              </ElButton>
              <ElButton
                size="small"
                type="warning"
                :disabled="loading || row.status !== 'ENABLED'"
                @click="handleUnload(row)"
              >
                弹出
              </ElButton>
            </div>
          </template>
        </ElTableColumn>
      </ElTable>
    </ElCard>
  </div>
</template>

<script setup lang="ts">
  import { fetchLoadPlugin, fetchPluginList, fetchUnloadPlugin } from '@/api/plugin'
  import { ElButton, ElMessage, ElMessageBox } from 'element-plus'
  import { onMounted, ref } from 'vue'

  defineOptions({ name: 'PluginManager' })

  const plugins = ref<Api.Plugin.Item[]>([])
  const loading = ref(false)

  const resolveErrorMessage = (error: unknown, fallback: string) => {
    if (error instanceof Error && error.message) {
      return error.message
    }
    if (typeof error === 'string' && error) {
      return error
    }
    return fallback
  }

  const loadPlugins = async () => {
    loading.value = true
    try {
      plugins.value = await fetchPluginList()
    } catch (error) {
      ElMessage.error(resolveErrorMessage(error, '插件列表加载失败'))
      console.error(error)
    } finally {
      loading.value = false
    }
  }

  const statusTag = (row: Api.Plugin.Item) => {
    if (!row.available) {
      return { type: 'danger' as const, text: '不可用' }
    }
    switch (row.status) {
      case 'ENABLED':
        return { type: 'success' as const, text: '已启用' }
      case 'DISABLED':
        return { type: 'info' as const, text: '已禁用' }
      default:
        return { type: 'warning' as const, text: '未载入' }
    }
  }

  const handleLoad = async (row: Api.Plugin.Item) => {
    try {
      await fetchLoadPlugin(row.pluginKey)
      ElMessage.success(`插件 ${row.name} 已载入`)
      await loadPlugins()
    } catch (error) {
      ElMessage.error(resolveErrorMessage(error, '载入插件失败'))
    }
  }

  const handleUnload = async (row: Api.Plugin.Item) => {
    try {
      await ElMessageBox.confirm(`确定要弹出插件 ${row.name} 吗？`, '提示', {
        type: 'warning'
      })
      await fetchUnloadPlugin(row.pluginKey)
      ElMessage.success(`插件 ${row.name} 已弹出`)
      await loadPlugins()
    } catch (error) {
      if (error !== 'cancel' && error !== 'close') {
        ElMessage.error(resolveErrorMessage(error, '弹出插件失败'))
      }
    }
  }

  onMounted(() => {
    loadPlugins()
  })
</script>

<style scoped>
  .plugin-page {
    display: flex;
    flex-direction: column;
  }

  .plugin-card {
    flex: 1;
    display: flex;
    flex-direction: column;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: 500;
  }

  .plugin-alert {
    margin-bottom: 12px;
  }

  .plugin-name {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .plugin-name__text {
    font-weight: 600;
  }

  .plugin-key {
    font-size: 12px;
    color: var(--el-text-color-secondary);
  }

  .script-list {
    display: flex;
    flex-direction: column;
    gap: 2px;
  }
</style>
