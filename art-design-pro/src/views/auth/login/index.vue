<template>
  <div class="login">
    <LoginLeftView></LoginLeftView>

    <div class="right-wrap">
      <AuthTopBar></AuthTopBar>
      <div class="header">
        <ArtLogo class="icon" />
        <h1>{{ systemName }}</h1>
      </div>
      <div class="login-wrap">
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
            <ElFormItem prop="account">
              <ElSelect v-model="formData.account" class="account-select" placeholder="请选择角色" clearable>
                <ElOption
                  v-for="role in displayRoleOptions"
                  :key="role.code"
                  :label="role.name"
                  :value="role.code"
                >
                  <span>{{ role.name }}</span>
                </ElOption>
              </ElSelect>
            </ElFormItem>
            <ElFormItem prop="username">
              <ElInput
                :placeholder="$t('login.placeholder.username')"
                v-model.trim="formData.username"
              />
            </ElFormItem>
            <ElFormItem prop="password">
              <ElInput
                :placeholder="$t('login.placeholder.password')"
                v-model.trim="formData.password"
                type="password"
                radius="8px"
                autocomplete="off"
                show-password
              />
            </ElFormItem>
            <div class="drag-verify">
              <div class="drag-verify-content" :class="{ error: !isPassing && isClickPass }">
                <ArtDragVerify
                  ref="dragVerify"
                  v-model:value="isPassing"
                  :text="$t('login.sliderText')"
                  textColor="var(--art-gray-800)"
                  :successText="$t('login.sliderSuccessText')"
                  :progressBarBg="getCssVar('--el-color-primary')"
                  background="var(--art-gray-200)"
                  handlerBg="var(--art-main-bg-color)"
                />
              </div>
              <p class="error-text" :class="{ 'show-error-text': !isPassing && isClickPass }">{{
                $t('login.placeholder.slider')
              }}</p>
            </div>

            <div class="forget-password">
              <ElCheckbox v-model="formData.rememberPassword">{{
                $t('login.rememberPwd')
              }}</ElCheckbox>
              <RouterLink :to="{ name: 'ForgetPassword' }">{{ $t('login.forgetPwd') }}</RouterLink>
            </div>

            <div style="margin-top: 30px">
              <ElButton
                class="login-btn"
                type="primary"
                @click="handleSubmit"
                :loading="loading"
                v-ripple
              >
                {{ $t('login.btnText') }}
              </ElButton>
            </div>

            <div class="footer">
              <p>
                {{ $t('login.noAccount') }}
                <RouterLink :to="{ name: 'Register' }">{{ $t('login.register') }}</RouterLink>
              </p>
            </div>
          </ElForm>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { computed, reactive, ref, watch, onMounted } from 'vue'
  import { useSystemConfigStore } from '@/store/modules/system-config'
  import { useUserStore } from '@/store/modules/user'
  import { getCssVar } from '@/utils/ui'
  import { useI18n } from 'vue-i18n'
  import { HttpError } from '@/utils/http/error'
  import { fetchLogin, fetchGetUserInfo } from '@/api/auth'
  import { ElNotification, type FormInstance, type FormRules } from 'element-plus'
  import { ROLE_OPTION_CACHE_KEY } from '@/constants/cacheKeys'

  defineOptions({ name: 'Login' })

  const { t, locale } = useI18n()
  const formKey = ref(0)

  // 监听语言切换，重置表单
  watch(locale, () => {
    formKey.value++
    syncRoleOptionsFromCache()
  })

  interface LoginRoleOption {
    code: string
    name: string
  }

  const systemConfigStore = useSystemConfigStore()
  const roleOptions = ref<LoginRoleOption[]>([])

  const defaultRoleOptions = computed<LoginRoleOption[]>(() => [
    { code: 'R_SUPER', name: t('login.roles.super') },
    { code: 'R_ADMIN', name: t('login.roles.admin') },
    { code: 'R_USER', name: t('login.roles.user') }
  ])

  const displayRoleOptions = computed<LoginRoleOption[]>(() =>
    roleOptions.value.length ? roleOptions.value : defaultRoleOptions.value
  )

  const syncRoleOptionsFromCache = () => {
    if (typeof window === 'undefined') {
      roleOptions.value = []
      return
    }
    try {
      const cache = window.localStorage.getItem(ROLE_OPTION_CACHE_KEY)
      if (!cache) {
        roleOptions.value = []
        return
      }
      const parsed = JSON.parse(cache)
      if (!Array.isArray(parsed)) {
        roleOptions.value = []
        return
      }
      const seen = new Set<string>()
      roleOptions.value = parsed
        .map((item: any) => {
          const code = typeof item.code === 'string' ? item.code.trim() : ''
          const name =
            typeof item.name === 'string' && item.name.trim().length
              ? item.name
              : code
          return { code, name }
        })
        .filter((item) => {
          if (!item.code || seen.has(item.code)) return false
          seen.add(item.code)
          return true
        })
    } catch (error) {
      roleOptions.value = []
    }
  }

  onMounted(() => {
    syncRoleOptionsFromCache()
  })

  const dragVerify = ref()

  const userStore = useUserStore()
  const router = useRouter()
  const isPassing = ref(false)
  const isClickPass = ref(false)

  const systemName = computed(() => systemConfigStore.brandName)
  const formRef = ref<FormInstance>()

  const formData = reactive({
    account: '',
    username: '',
    password: '',
    rememberPassword: true
  })

  const rules = computed<FormRules>(() => ({
    account: [{ required: true, message: t('login.placeholder.selectRole'), trigger: 'change' }],
    username: [{ required: true, message: t('login.placeholder.username'), trigger: 'blur' }],
    password: [{ required: true, message: t('login.placeholder.password'), trigger: 'blur' }]
  }))

  const loading = ref(false)

  // 默认不填充任何账号信息，保持用户名与密码为空

  // 选择账号角色不再自动填充用户名与密码，保留用户手动输入

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
      const { account: selectedRoleCode, username, password } = formData

      // 登录类型校验：必须选择类型
      if (!selectedRoleCode) {
        throw new Error(t('login.placeholder.selectRole'))
      }

      const { token, refreshToken } = await fetchLogin({
        userName: username,
        password
      })

      // 验证token
      if (!token) {
        throw new Error('Login failed - no token received')
      }

      // 存储token和用户信息
      userStore.setToken(token, refreshToken)
      const userInfo = await fetchGetUserInfo()
      userStore.setUserInfo(userInfo)
      userStore.setLoginStatus(true)

      // 登录类型与角色校验：不一致则报错并登出
      const userRoles = Array.isArray(userInfo.roles) ? userInfo.roles : []
      if (selectedRoleCode && userRoles.length && !userRoles.includes(selectedRoleCode)) {
        userStore.logOut()
        throw new Error(t('httpMsg.forbidden') || '登录类型与账号权限不一致')
      }

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
        message: `${t('login.success.message')}, ${systemName.value}!`
      })
    }, 150)
  }
</script>

<style lang="scss" scoped>
  @use './index';
</style>
