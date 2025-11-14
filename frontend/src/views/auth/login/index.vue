<!-- 登录页面 -->
<template>
  <div class="flex w-full h-screen">
    <LoginLeftView />

    <div class="relative flex-1">
      <AuthTopBar />

      <div class="auth-right-wrap">
        <div class="form">
          <h3 class="title">{{ $t('login.title') }}</h3>
          <p class="sub-title">{{ $t('login.subTitle') }}</p>
          <ElForm
            ref="formRef"
            :model="formData"
            :rules="rules"
            :key="formKey"
            @keyup.enter="handleSubmit"
            style="margin-top: 25px"
          >
            
            <ElFormItem prop="username">
              <ElInput
                class="custom-height"
                :placeholder="$t('login.placeholder.username')"
                v-model.trim="formData.username"
              />
            </ElFormItem>
            <ElFormItem prop="password">
              <ElInput
                class="custom-height"
                :placeholder="$t('login.placeholder.password')"
                v-model.trim="formData.password"
                type="password"
                autocomplete="off"
                show-password
              />
            </ElFormItem>
            <ElFormItem prop="roleCode">
              <ElSelect
                class="w-full custom-height"
                v-model="formData.roleCode"
                placeholder="请选择角色"
                :disabled="rolesLoading || roleOptions.length === 0"
              >
                <ElOption
                  v-for="r in roleOptions"
                  :key="r.roleCode"
                  :label="r.roleName"
                  :value="r.roleCode"
                />
              </ElSelect>
            </ElFormItem>

            <!-- 推拽验证 -->
            <div class="relative pb-5 mt-6">
              <div
                class="relative z-[2] overflow-hidden select-none rounded-lg border border-transparent tad-300"
                :class="{ '!border-[#FF4E4F]': !isPassing && isClickPass }"
              >
                <ArtDragVerify
                  ref="dragVerify"
                  v-model:value="isPassing"
                  :text="$t('login.sliderText')"
                  textColor="var(--art-gray-700)"
                  :successText="$t('login.sliderSuccessText')"
                  :progressBarBg="getCssVar('--el-color-primary')"
                  :background="isDark ? '#26272F' : '#F1F1F4'"
                  handlerBg="var(--default-box-color)"
                />
              </div>
              <p
                class="absolute top-0 z-[1] px-px mt-2 text-xs text-[#f56c6c] tad-300"
                :class="{ 'translate-y-10': !isPassing && isClickPass }"
              >
                {{ $t('login.placeholder.slider') }}
              </p>
            </div>

            <div class="flex-cb mt-2 text-sm">
              <ElCheckbox v-model="formData.rememberPassword">{{
                $t('login.rememberPwd')
              }}</ElCheckbox>
              <RouterLink class="text-theme" :to="{ name: 'ForgetPassword' }">{{
                $t('login.forgetPwd')
              }}</RouterLink>
            </div>

            <div style="margin-top: 30px">
              <ElButton
                class="w-full custom-height"
                type="primary"
                @click="handleSubmit"
                :loading="loading"
                v-ripple
              >
                {{ $t('login.btnText') }}
              </ElButton>
            </div>

            <div class="mt-5 text-sm text-gray-600">
              <span>{{ $t('login.noAccount') }}</span>
              <RouterLink class="text-theme" :to="{ name: 'Register' }">{{
                $t('login.register')
              }}</RouterLink>
            </div>
          </ElForm>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import AppConfig from '@/config'
  import { useUserStore } from '@/store/modules/user'
  import { getCssVar } from '@/utils/ui'
  import { useI18n } from 'vue-i18n'
  import { HttpError } from '@/utils/http/error'
  import { fetchLogin, fetchBootstrap, fetchUserRoles } from '@/api/auth'
  import { ElNotification, type FormInstance, type FormRules } from 'element-plus'
  import { useSettingStore } from '@/store/modules/setting'
  import { useMenuStore } from '@/store/modules/menu'

  defineOptions({ name: 'Login' })

  const settingStore = useSettingStore()
  const { isDark } = storeToRefs(settingStore)
  const { t, locale } = useI18n()
  const formKey = ref(0)

  // 监听语言切换，重置表单
  watch(locale, () => {
    formKey.value++
  })

  

  const dragVerify = ref()

  const userStore = useUserStore()
  const router = useRouter()
  const isPassing = ref(false)
  const isClickPass = ref(false)

  const systemName = AppConfig.systemInfo.name
  const formRef = ref<FormInstance>()

  const formData = reactive({
    username: '',
    password: '',
    roleCode: '',
    rememberPassword: true
  })
  const roleOptions = ref<{ roleName: string; roleCode: string }[]>([])
  const rolesLoading = ref(false)
  let rolesLoadTimer: any = null
  const loadRoles = async () => {
    formData.roleCode = ''
    roleOptions.value = []
    if (!formData.username) return
    rolesLoading.value = true
    try {
      const { roles } = await fetchUserRoles(formData.username)
      roleOptions.value = (roles || []).map((r: any) => ({ roleName: r.roleName, roleCode: r.roleCode }))
    } finally {
      rolesLoading.value = false
    }
  }
  watch(
    () => formData.username,
    (v, ov) => {
      if (!v || v === ov) return
      if (rolesLoadTimer) clearTimeout(rolesLoadTimer)
      rolesLoadTimer = setTimeout(loadRoles, 300)
    }
  )

  const rules = computed<FormRules>(() => ({
    username: [{ required: true, message: t('login.placeholder.username'), trigger: 'blur' }],
    password: [{ required: true, message: t('login.placeholder.password'), trigger: 'blur' }],
    roleCode: [{ required: true, message: '请选择角色', trigger: 'change' }]
  }))

  const loading = ref(false)

  onMounted(() => {})

  

  // 登录
  const handleSubmit = async () => {
    if (!formRef.value) return

    try {
      // 表单验证
      const valid = await formRef.value.validate()
      if (!valid) return

      // 拖拽验证
      if (!isPassing.value) {
        isClickPass.value = true
        return
      }

      loading.value = true

      // 登录请求
      const { username, password, roleCode } = formData

      const { token, user } = await fetchLogin({ userName: username, password, roleCode })

      // 验证token
      if (!token) {
        throw new Error('Login failed - no token received')
      }

      // 存储token和用户信息
      userStore.setToken(token)
      let raw: any = user || null
      let boot: any = null
      try {
        boot = await fetchBootstrap()
        raw = raw || boot.user
      } catch {}
      const roleCodes = Array.isArray((raw as any)?.roles)
        ? ((raw as any).roles as any[]).map((r: any) => r?.roleCode || r).map((c: string) => (c === 'ADMIN' ? 'R_ADMIN' : c === 'USER' ? 'R_USER' : c))
        : []
      userStore.setUserInfo({ ...(raw as any), roles: roleCodes })
      if (Array.isArray((raw as any)?.permissions)) {
        userStore.setPermissions((raw as any).permissions as string[])
      } else {
        userStore.setPermissions([])
      }
      if (boot && Array.isArray((boot as any).menus)) {
        const menuStore = useMenuStore()
        menuStore.setMenuList((boot as any).menus || [])
        menuStore.setHomePath('/profile')
      }
      userStore.setLoginStatus(true)

      // 登录成功处理
      showLoginSuccessNotice()
      router.push('/')
    } catch (error) {
      // 处理 HttpError
      if (error instanceof HttpError) {
        // console.log(error.code)
      } else {
        // 处理非 HttpError
        // ElMessage.error('登录失败，请稍后重试')
        console.error('[Login] Unexpected error:', error)
      }
    } finally {
      loading.value = false
      resetDragVerify()
    }
  }

  // 重置拖拽验证
  const resetDragVerify = () => {
    dragVerify.value.reset()
  }

  // 登录成功提示
  const showLoginSuccessNotice = () => {
    setTimeout(() => {
      ElNotification({
        title: t('login.success.title'),
        type: 'success',
        duration: 2500,
        zIndex: 10000,
        message: `${t('login.success.message')}, ${systemName}!`
      })
    }, 150)
  }
</script>

<style scoped>
  @import './style.css';
</style>

<style lang="scss" scoped>
</style>
