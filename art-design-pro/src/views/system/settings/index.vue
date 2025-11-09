<template>
  <div class="page-content system-settings">
    <ElRow :gutter="20">
      <ElCol :md="12" :sm="24">
        <ElCard shadow="never">
          <template #header>品牌设置</template>
          <ElForm label-width="80px">
            <ElFormItem label="系统名称">
              <ElInput v-model.trim="brandForm.name" placeholder="请输入系统名称" />
            </ElFormItem>
            <ElFormItem label="Logo文件">
              <ElUpload
                class="logo-upload"
                :accept="'image/*'"
                :before-upload="handleLogoUpload"
                :show-file-list="false"
              >
                <ElButton type="primary">
                  <ElIcon>
                    <UploadFilled />
                  </ElIcon>
                  <span>选择文件</span>
                </ElButton>
              </ElUpload>
            </ElFormItem>
            <ElFormItem label="Logo预览">
              <div class="logo-preview">
                <ElImage
                  class="logo-image"
                  v-if="brandForm.logoUrl"
                  :src="brandForm.logoUrl"
                  fit="contain"
                  preview-teleported
                />
                <span v-else>使用默认logo</span>
              </div>
            </ElFormItem>
            <ElFormItem>
              <ElButton type="primary" :loading="brandSaving" @click="saveBrand" v-ripple style="width: 120px;">
                保存品牌
              </ElButton>
            </ElFormItem>
          </ElForm>
        </ElCard>
      </ElCol>

      <ElCol :md="12" :sm="24">
        <ElCard shadow="never">
          <template #header>全局水印</template>
          <ElForm label-width="90px">
            <ElFormItem label="启用水印">
              <ElSwitch v-model="watermarkForm.enabled" />
            </ElFormItem>
            <ElFormItem label="显示内容">
              <ElRadioGroup v-model="watermarkForm.mode">
                <ElRadioButton label="username">显示用户姓名</ElRadioButton>
                <ElRadioButton label="custom">自定义文字</ElRadioButton>
              </ElRadioGroup>
            </ElFormItem>
            <ElFormItem label="自定义文字" v-if="watermarkForm.mode === 'custom'">
              <ElInput v-model.trim="watermarkForm.customText" placeholder="请输入水印文字" />
            </ElFormItem>
            <ElFormItem label="字体大小">
              <ElInputNumber v-model="watermarkForm.fontSize" :min="10" :max="48" />
            </ElFormItem>
            <ElFormItem>
              <ElButton type="primary" :loading="watermarkSaving" @click="saveWatermark" v-ripple>
                保存水印
              </ElButton>
            </ElFormItem>
          </ElForm>
        </ElCard>
      </ElCol>
    </ElRow>
  </div>
</template>

<script setup lang="ts">
  import AppConfig from '@/config'
  import type { UploadRawFile } from 'element-plus'
  import { uploadBrandLogo } from '@/api/system-setting'
  import { onMounted, reactive, ref, watch } from 'vue'
  import { ElMessage } from 'element-plus'
  import { fetchSystemSettings, fetchUpdateBrandSetting, fetchUpdateWatermarkSetting } from '@/api/system-setting'
  import { useSystemConfigStore } from '@/store/modules/system-config'
  import { UploadFilled } from '@element-plus/icons-vue'

  defineOptions({ name: 'SystemSettings' })

  const systemConfigStore = useSystemConfigStore()

  const defaultLogo = AppConfig.systemInfo.logo || ''
  const brandForm = reactive({
    name: systemConfigStore.brandName,
    logoUrl: systemConfigStore.logoUrl || defaultLogo
  })
  const watermarkForm = reactive({
    enabled: systemConfigStore.watermark.enabled,
    mode: systemConfigStore.watermark.mode,
    customText: systemConfigStore.watermark.customText,
    fontSize: systemConfigStore.watermark.fontSize
  })

  const brandSaving = ref(false)
  const watermarkSaving = ref(false)

  const loadSettings = async () => {
    try {
      const data = await fetchSystemSettings()
      brandForm.name = data.brand?.name || brandForm.name
      watermarkForm.enabled = !!data.watermark?.enabled
      watermarkForm.mode = (data.watermark?.mode as 'username' | 'custom') || 'username'
      watermarkForm.customText = data.watermark?.customText || ''
      watermarkForm.fontSize = data.watermark?.fontSize || 16
      systemConfigStore.brandName = brandForm.name
      const fetchedLogo = data.brand?.logoUrl?.trim()
      systemConfigStore.logoUrl = fetchedLogo ? fetchedLogo : defaultLogo
      brandForm.logoUrl = fetchedLogo ? fetchedLogo : defaultLogo
      systemConfigStore.watermark.enabled = watermarkForm.enabled
      systemConfigStore.watermark.mode = watermarkForm.mode
      systemConfigStore.watermark.customText = watermarkForm.customText
      systemConfigStore.watermark.fontSize = watermarkForm.fontSize
    } catch (error) {
      console.error('加载系统设置失败', error)
    }
  }

  const saveBrand = async () => {
    if (!brandForm.name.trim()) {
      ElMessage.error('请输入系统名称')
      return
    }
    brandSaving.value = true
    try {
      const data = await fetchUpdateBrandSetting({
        name: brandForm.name,
        logoUrl: brandForm.logoUrl
      })
      systemConfigStore.brandName = data.name
      ElMessage.success('品牌设置已更新')
      const updatedLogo = data.logoUrl?.trim()
      systemConfigStore.logoUrl = updatedLogo ? updatedLogo : defaultLogo
    } finally {
      brandSaving.value = false
    }
  }

  const saveWatermark = async () => {
    if (watermarkForm.mode === 'custom' && !watermarkForm.customText.trim()) {
      ElMessage.error('请输入自定义水印文字')
      return
    }
    watermarkSaving.value = true
    try {
      const data = await fetchUpdateWatermarkSetting({
        enabled: watermarkForm.enabled,
        mode: watermarkForm.mode,
        customText: watermarkForm.customText,
        fontSize: watermarkForm.fontSize
      })
      systemConfigStore.watermark.enabled = data.enabled
      systemConfigStore.watermark.mode = data.mode as 'username' | 'custom'
      systemConfigStore.watermark.customText = data.customText || ''
      systemConfigStore.watermark.fontSize = data.fontSize || 16
      ElMessage.success('水印设置已更新')
    } finally {
      watermarkSaving.value = false
    }
  }

  watch(
    () => systemConfigStore.brandName,
    (val) => {
      brandForm.name = val
    }
  )

  watch(
    () => systemConfigStore.logoUrl,
    (val) => {
      brandForm.logoUrl = val || defaultLogo
    }
  )

  const handleLogoUpload = async (file: UploadRawFile) => {
    const rawFile = file as File
    if (!rawFile) return false
    try {
      const data = await uploadBrandLogo(rawFile as File)
      brandForm.logoUrl = data.url || ''
    } catch (error) {
      // 处理错误即可，UI 会显示失败
    }
    return false
  }

  watch(
    () => [
      systemConfigStore.watermark.enabled,
      systemConfigStore.watermark.mode,
      systemConfigStore.watermark.customText,
      systemConfigStore.watermark.fontSize
    ],
    ([enabled, mode, customText, fontSize]) => {
      watermarkForm.enabled = enabled
      watermarkForm.mode = mode as 'username' | 'custom'
      watermarkForm.customText = customText
      watermarkForm.fontSize = fontSize
    }
  )

  onMounted(() => {
    loadSettings()
  })
</script>

<style lang="scss" scoped>
  .system-settings {
    .el-card {
      margin-bottom: 20px;
    }
    .el-form {
      max-width: 420px;
    }
    .logo-preview {
      display: flex;
      align-items: center;
      min-height: 80px;
      .logo-image {
        max-height: 80px;
        max-width: 240px;
        border-radius: 6px;
        box-shadow: 0 0 8px rgba(0, 0, 0, 0.05);
      }
    }
    .logo-upload {
      width: 120px;
      .el-button {
        width: 100%;
        height: 36px;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        gap: 6px;
      }
    }
  }
</style>
