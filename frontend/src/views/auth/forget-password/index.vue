<template>
  <div class="flex w-full h-screen">
    <LoginLeftView />

    <div class="relative flex-1">
      <AuthTopBar />

      <div class="auth-right-wrap">
        <div class="form">
          <h3 class="title">{{ $t('forgetPassword.title') }}</h3>
          <p class="sub-title">{{ $t('forgetPassword.subTitle') }}</p>
          <div class="mt-5">
            <span class="input-label" v-if="showInputLabel">账号</span>
            <ElInput class="custom-height" :placeholder="$t('forgetPassword.placeholder')" v-model.trim="username" />
          </div>
          <div style="margin-top: 15px">
            <ElInput class="custom-height" placeholder="新密码" type="password" v-model.trim="newPassword" show-password />
          </div>

          <div style="margin-top: 15px">
            <ElButton
              class="w-full custom-height"
              type="primary"
              @click="submit"
              :loading="loading"
              v-ripple
            >
              {{ $t('forgetPassword.submitBtnText') }}
            </ElButton>
          </div>

          <div style="margin-top: 15px">
            <ElButton class="w-full custom-height" plain @click="toLogin">
              {{ $t('forgetPassword.backBtnText') }}
            </ElButton>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  defineOptions({ name: 'ForgetPassword' })

  const router = useRouter()
  const showInputLabel = ref(false)

  const username = ref('')
  const newPassword = ref('')
  const loading = ref(false)

  const submit = async () => {
    if (!username.value || !newPassword.value) return
    loading.value = true
    try {
      const api = await import('@/api/auth')
      await api.fetchForgotPassword({ username: username.value, newPassword: newPassword.value })
      ElMessage.success('密码重置成功，请使用新密码登录')
      toLogin()
    } finally {
      loading.value = false
    }
  }

  const toLogin = () => {
    router.push({ name: 'Login' })
  }
</script>

<style scoped>
  @import '../login/style.css';
</style>
