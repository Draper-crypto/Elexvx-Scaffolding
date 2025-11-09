import { defineStore } from 'pinia'
import { ref, reactive, computed, watch } from 'vue'
import AppConfig from '@/config'
import { fetchPublicSystemConfig } from '@/api/system-setting'
import { useUserStore } from './user'
import { useSettingStore } from './setting'

interface WatermarkConfig {
  enabled: boolean
  mode: 'username' | 'custom'
  customText: string
  fontSize: number
}

export const useSystemConfigStore = defineStore(
  'systemConfig',
  () => {
    const brandName = ref(AppConfig.systemInfo.name)
    const logoUrl = ref(AppConfig.systemInfo.logo || '')
    const watermark = reactive<WatermarkConfig>({
      enabled: true,
      mode: 'username',
      customText: '',
      fontSize: 16
    })
    const initialized = ref(false)

    const userStore = useUserStore()
    const settingStore = useSettingStore()

    const resolvedWatermarkText = computed(() => {
      if (watermark.mode === 'custom' && watermark.customText.trim()) {
        return watermark.customText
      }
      const info = userStore.getUserInfo
      return (
        info.fullName ||
        info.nickname ||
        info.userName ||
        brandName.value
      )
    })

    const effectiveWatermarkVisible = computed(() => {
      return watermark.enabled && settingStore.watermarkVisible
    })

    const loadPublicConfig = async () => {
      try {
        const data = await fetchPublicSystemConfig()
        if (data.brand?.name) {
          brandName.value = data.brand.name
        }
        if (data.brand?.logoUrl) {
          logoUrl.value = data.brand.logoUrl
        } else {
          logoUrl.value = AppConfig.systemInfo.logo || ''
        }
        if (data.watermark) {
          watermark.enabled = !!data.watermark.enabled
          watermark.mode = (data.watermark.mode as WatermarkConfig['mode']) || 'username'
          watermark.customText = data.watermark.customText || ''
          watermark.fontSize = data.watermark.fontSize || 16
        }
        settingStore.setWatermarkVisible(watermark.enabled)
      } finally {
        initialized.value = true
      }
    }

    watch(
      () => watermark.enabled,
      (enabled) => {
        if (!enabled) {
          settingStore.setWatermarkVisible(false)
        }
      }
    )

    return {
      brandName,
      logoUrl,
      watermark,
      resolvedWatermarkText,
      effectiveWatermarkVisible,
      initialized,
      loadPublicConfig
    }
  },
  {
    persist: {
      key: 'system-config',
      storage: localStorage
    }
  }
)
