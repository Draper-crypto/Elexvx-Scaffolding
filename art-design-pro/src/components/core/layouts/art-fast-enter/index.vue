<!-- 顶部快速入口面板 -->
<template>
  <ElPopover
    ref="popoverRef"
    :width="700"
    trigger="hover"
    popper-class="fast-enter-popover"
    :show-arrow="false"
    placement="bottom-start"
    :offset="0"
    :popper-style="{
      border: '1px solid var(--art-border-dashed-color)',
      borderRadius: 'calc(var(--custom-radius) / 2 + 4px)'
    }"
  >
    <template #reference>
      <div class="fast-enter-trigger">
        <div class="btn">
          <i class="iconfont-sys">&#xe81a;</i>
        </div>
      </div>
    </template>

    <div class="fast-enter">
      <div class="apps-section">
        <div class="apps-grid">
          <!-- 应用列表 -->
          <div
            v-for="application in enabledApplications"
            :key="application.name"
            class="app-item"
            @click="handleApplicationClick(application)"
          >
            <div class="app-icon">
              <i
                class="iconfont-sys"
                v-html="application.icon"
                :style="{ color: application.iconColor }"
              />
            </div>
            <div class="app-info">
              <h3>{{ application.name }}</h3>
              <p>{{ application.description }}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="quick-links">
        <h3>快速链接</h3>
        <ul>
          <li
            v-for="quickLink in enabledQuickLinks"
            :key="quickLink.name"
            @click="handleQuickLinkClick(quickLink)"
          >
            <span>{{ quickLink.name }}</span>
          </li>
        </ul>
      </div>
    </div>
  </ElPopover>
</template>

<script setup lang="ts">
  import { useFastEnter } from '@/composables/useFastEnter'
  import type { FastEnterApplication, FastEnterQuickLink } from '@/types/config'

  defineOptions({ name: 'ArtFastEnter' })

  const router = useRouter()
  const popoverRef = ref()

  // 使用快速入口配置
  const { enabledApplications, enabledQuickLinks } = useFastEnter()

  /**
   * 处理导航跳转
   * @param routeName 路由名称
   * @param link 外部链接
   */
  const handleNavigate = (routeName?: string, routePath?: string, link?: string): void => {
    if (link) {
      if (link.startsWith('http')) {
        window.open(link, '_blank')
      } else {
        router.push(link)
      }
      popoverRef.value?.hide()
      return
    }

    if (routePath) {
      router.push(routePath)
      popoverRef.value?.hide()
      return
    }

    if (routeName) {
      router.push({ name: routeName })
      popoverRef.value?.hide()
      return
    }

    console.warn('导航配置无效：需至少提供路由或链接')
  }

  const handleApplicationClick = (application: FastEnterApplication): void => {
    handleNavigate(application.routeName, application.routePath, application.link)
  }

  /**
   * 处理快速链接点击
   * @param quickLink 快速链接配置对象
   */
  const handleQuickLinkClick = (quickLink: FastEnterQuickLink): void => {
    handleNavigate(quickLink.routeName, quickLink.routePath, quickLink.link)
  }
</script>

<style lang="scss" scoped>
  @use './style';
</style>
