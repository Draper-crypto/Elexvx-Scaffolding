<template>
  <div class="page-content user">
    <div class="content">
      <div class="left-wrap">
        <div class="user-wrap box-style">
          <img class="bg" src="@imgs/user/bg.webp" />
          <div class="avatar-wrapper">
            <img class="avatar" :src="avatarSrc" />
            <div class="avatar-overlay" v-if="avatarUploading">
              <i class="el-icon-loading"></i>
            </div>
          </div>
          <div class="avatar-action">
            <ElUpload
              :before-upload="handleAvatarUpload"
              :show-file-list="false"
              :accept="'image/*'"
            >
              <ElButton type="primary" :loading="avatarUploading" v-ripple>
                更换头像
              </ElButton>
            </ElUpload>
          </div>
          <h2 class="name">{{ form.realName || userInfo.displayName || userInfo.userName }}</h2>
          <p class="des">{{ form.des || defaultBio }}</p>

          <div class="outer-info">
            <div>
              <i class="iconfont-sys">&#xe72e;</i>
              <span>{{ form.email || userInfo.email || '未填写' }}</span>
            </div>
            <div>
              <i class="iconfont-sys">&#xe608;</i>
              <span>{{ form.nikeName || '交互专家' }}</span>
            </div>
            <div>
              <i class="iconfont-sys">&#xe736;</i>
              <span>{{ form.address || '未填写地址' }}</span>
            </div>
            <div>
              <i class="iconfont-sys">&#xe811;</i>
              <span>{{ form.mobile || '未填写电话' }}</span>
            </div>
          </div>

          <div class="lables">
            <h3>标签</h3>
            <div>
              <div v-for="item in lableList" :key="item">
                {{ item }}
              </div>
            </div>
          </div>
        </div>

        <!-- <ElCarousel class="gallery" height="160px"
          :interval="5000"
          indicator-position="none"
        >
          <ElCarouselItem class="item" v-for="item in galleryList" :key="item">
            <img :src="item"/>
          </ElCarouselItem>
        </ElCarousel> -->
      </div>
      <div class="right-wrap">
        <div class="info box-style">
          <h1 class="title">基本设置</h1>

          <ElForm
            :model="form"
            class="form"
            ref="ruleFormRef"
            :rules="rules"
            label-width="86px"
            label-position="top"
          >
            <ElRow>
              <ElFormItem label="姓名" prop="realName">
                <ElInput v-model="form.realName" :disabled="!isEdit" />
              </ElFormItem>
              <ElFormItem label="性别" prop="sex" class="right-input">
                <ElSelect v-model="form.sex" placeholder="Select" :disabled="!isEdit">
                  <ElOption
                    v-for="item in options"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </ElSelect>
              </ElFormItem>
            </ElRow>

            <ElRow>
              <ElFormItem label="昵称" prop="nikeName">
                <ElInput v-model="form.nikeName" :disabled="!isEdit" />
              </ElFormItem>
              <ElFormItem label="邮箱" prop="email" class="right-input">
                <ElInput v-model="form.email" :disabled="!isEdit" />
              </ElFormItem>
            </ElRow>

            <ElRow>
              <ElFormItem label="手机" prop="mobile">
                <ElInput v-model="form.mobile" :disabled="!isEdit" />
              </ElFormItem>
              <ElFormItem label="地址" prop="address" class="right-input">
                <ElInput v-model="form.address" :disabled="!isEdit" />
              </ElFormItem>
            </ElRow>

            <ElFormItem label="个人介绍" prop="des" :style="{ height: '130px' }">
              <ElInput type="textarea" :rows="4" v-model="form.des" :disabled="!isEdit" />
            </ElFormItem>

            <div class="el-form-item-right">
              <ElButton type="primary" style="width: 90px" v-ripple @click="edit">
                {{ isEdit ? '保存' : '编辑' }}
              </ElButton>
            </div>
          </ElForm>
        </div>

        <div class="info box-style" style="margin-top: 20px">
          <h1 class="title">更改密码</h1>

          <ElForm
            ref="pwdFormRef"
            :model="pwdForm"
            :rules="pwdRules"
            class="form"
            label-width="86px"
            label-position="top"
          >
            <ElFormItem label="当前密码" prop="password">
              <ElInput
                v-model="pwdForm.password"
                type="password"
                :disabled="!isEditPwd"
                show-password
              />
            </ElFormItem>

            <ElFormItem label="新密码" prop="newPassword">
              <ElInput
                v-model="pwdForm.newPassword"
                type="password"
                :disabled="!isEditPwd"
                show-password
              />
            </ElFormItem>

            <ElFormItem label="确认新密码" prop="confirmPassword">
              <ElInput
                v-model="pwdForm.confirmPassword"
                type="password"
                :disabled="!isEditPwd"
                show-password
              />
            </ElFormItem>

            <div class="el-form-item-right">
              <ElButton type="primary" style="width: 90px" v-ripple @click="editPwd">
                {{ isEditPwd ? '保存' : '编辑' }}
              </ElButton>
            </div>
          </ElForm>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import defaultAvatar from '@imgs/user/avatar.webp'
  import { computed, reactive, ref, onMounted } from 'vue'
  import { useUserStore } from '@/store/modules/user'
  import type { FormInstance, FormRules, UploadRawFile } from 'element-plus'
  import { ElMessage, ElUpload } from 'element-plus'
  import { fetchChangePassword, fetchUpdateProfile, fetchUserProfile, uploadProfileAvatar } from '@/api/profile'
  import { useSystemConfigStore } from '@/store/modules/system-config'

  defineOptions({ name: 'UserCenter' })

  const userStore = useUserStore()
  const systemConfigStore = useSystemConfigStore()
  const userInfo = computed(() => userStore.getUserInfo)
  const defaultBio = computed(() => `${systemConfigStore.brandName} 是一款兼具设计美学与高效开发的后台系统。`)
  const avatarSrc = computed(() => userInfo.value.avatar || defaultAvatar)

  const isEdit = ref(false)
  const isEditPwd = ref(false)
  const date = ref('')
  const ruleFormRef = ref<FormInstance>()
  const pwdFormRef = ref<FormInstance>()
  const avatarUploading = ref(false)

  /**
   * 用户信息表单
   */
  const form = reactive({
    realName: '',
    nikeName: '',
    email: '',
    mobile: '',
    address: '',
    sex: '',
    des: defaultBio.value
  })

  /**
   * 密码修改表单
   */
  const pwdForm = reactive({
    password: '',
    newPassword: '',
    confirmPassword: ''
  })

  /**
   * 表单验证规则
   */
  const rules = reactive<FormRules>({
    realName: [
      { required: true, message: '请输入姓名', trigger: 'blur' },
      { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
    ],
    nikeName: [
      { required: true, message: '请输入昵称', trigger: 'blur' },
      { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
    ],
    email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }],
    mobile: [{ required: true, message: '请输入手机号码', trigger: 'blur' }],
    address: [{ required: true, message: '请输入地址', trigger: 'blur' }],
    sex: [{ required: true, message: '请选择性别', trigger: 'blur' }]
  })

  const pwdRules = reactive<FormRules>({
    password: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
    newPassword: [
      { required: true, message: '请输入新密码', trigger: 'blur' },
      { min: 6, message: '新密码至少6位', trigger: 'blur' }
    ],
    confirmPassword: [
      {
        validator: (_rule, value, callback) => {
          if (!value) {
            callback(new Error('请确认新密码'))
            return
          }
          if (value !== pwdForm.newPassword) {
            callback(new Error('两次输入的密码不一致'))
            return
          }
          callback()
        },
        trigger: 'blur'
      }
    ]
  })

  /**
   * 性别选项
   */
  const options = [
    { value: '1', label: '男' },
    { value: '2', label: '女' }
  ]

  /**
   * 用户标签列表
   */
  const lableList: Array<string> = ['专注设计', '很有想法', '辣~', '大长腿', '川妹子', '海纳百川']

  const loadProfile = async () => {
    try {
      const data = await fetchUserProfile()
      form.realName = data.name || ''
      form.nikeName = data.nickname || ''
      form.email = data.email || ''
      form.mobile = data.phone || ''
      form.address = data.address || ''
      form.sex = data.gender != null ? String(data.gender) : ''
      form.des = data.bio || defaultBio.value
      userStore.setUserInfo({
        ...userStore.getUserInfo,
        fullName: data.name,
        nickname: data.nickname,
        displayName: data.nickname || data.name,
        email: data.email,
        avatar: data.avatarUrl
      } as Api.Auth.UserInfo)
    } catch (error) {
      console.error(error)
    }
  }

  onMounted(() => {
    getDate()
    loadProfile()
  })

  const handleAvatarUpload = async (file: UploadRawFile) => {
    const rawFile = file as File
    if (!rawFile) return false
    avatarUploading.value = true
    try {
      const avatarUrl = await uploadProfileAvatar(rawFile as File)
      userStore.setUserInfo({
        ...userStore.getUserInfo,
        avatar: avatarUrl
      } as Api.Auth.UserInfo)
      ElMessage.success('头像已更新')
    } catch (error) {
      console.error('上传头像失败', error)
    } finally {
      avatarUploading.value = false
    }
    return false
  }

  /**
   * 根据当前时间获取问候语
   */
  const getDate = () => {
    const h = new Date().getHours()

    if (h >= 6 && h < 9) date.value = '早上好'
    else if (h >= 9 && h < 11) date.value = '上午好'
    else if (h >= 11 && h < 13) date.value = '中午好'
    else if (h >= 13 && h < 18) date.value = '下午好'
    else if (h >= 18 && h < 24) date.value = '晚上好'
    else date.value = '很晚了，早点睡'
  }

  /**
   * 切换用户信息编辑状态
   */
  const edit = async () => {
    if (!isEdit.value) {
      isEdit.value = true
      return
    }
    if (!ruleFormRef.value) return
    await ruleFormRef.value.validate(async (valid) => {
      if (!valid) return
      await fetchUpdateProfile({
        name: form.realName,
        nickname: form.nikeName,
        email: form.email,
        phone: form.mobile,
        address: form.address,
        gender: form.sex ? Number(form.sex) : undefined,
        bio: form.des
      })
      ElMessage.success('资料已更新')
      isEdit.value = false
      loadProfile()
    })
  }

  /**
   * 切换密码编辑状态
   */
  const editPwd = async () => {
    if (!isEditPwd.value) {
      isEditPwd.value = true
      return
    }
    if (!pwdFormRef.value) return
    await pwdFormRef.value.validate(async (valid) => {
      if (!valid) return
      await fetchChangePassword({
        currentPassword: pwdForm.password,
        newPassword: pwdForm.newPassword,
        confirmPassword: pwdForm.confirmPassword
      })
      ElMessage.success('密码修改成功')
      isEditPwd.value = false
      Object.assign(pwdForm, { password: '', newPassword: '', confirmPassword: '' })
    })
  }
</script>

<style lang="scss">
  .user {
    .icon {
      width: 1.4em;
      height: 1.4em;
      overflow: hidden;
      vertical-align: -0.15em;
      fill: currentcolor;
    }
  }
</style>

<style lang="scss" scoped>
  .page-content {
    width: 100%;
    height: 100%;
    padding: 0 !important;
    background: transparent !important;
    border: none !important;
    box-shadow: none !important;

    $box-radius: calc(var(--custom-radius) + 4px);

    .box-style {
      border: 1px solid var(--art-border-color);
    }

    .content {
      position: relative;
      display: flex;
      justify-content: space-between;
      margin-top: 10px;

      .left-wrap {
        width: 450px;
        margin-right: 25px;

        .user-wrap {
          position: relative;
          min-height: 560px;
          padding: 35px 40px 50px;
          overflow: hidden;
          text-align: center;
          background: var(--art-main-bg-color);
          border-radius: $box-radius;

          .bg {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 200px;
            object-fit: cover;
          }

          .avatar-wrapper {
            position: relative;
            margin: 0 auto;
            width: 110px;
            height: 110px;
            margin-top: 80px;
            cursor: pointer;

            .avatar {
              width: 100%;
              height: 100%;
              object-fit: cover;
              border: 3px solid #fff;
              border-radius: 50%;
              display: block;
            }

            .avatar-overlay {
              position: absolute;
              inset: 0;
              background: rgba(0, 0, 0, 0.4);
              border-radius: 50%;
              display: flex;
              align-items: center;
              justify-content: center;
              color: #fff;
              font-size: 18px;
            }
          }

          .avatar-action {
            margin-top: 10px;
            display: flex;
            justify-content: center;

            .el-button {
              padding: 6px 40px;
              font-size: 14px;
            }
          }

          .name {
            margin-top: 20px;
            font-size: 22px;
            font-weight: 400;
          }

          .des {
            margin-top: 20px;
            font-size: 14px;
          }

          .outer-info {
            width: 300px;
            margin: auto;
            margin-top: 30px;
            text-align: left;

            > div {
              margin-top: 10px;

              span {
                margin-left: 8px;
                font-size: 14px;
              }
            }
          }

          .lables {
            margin-top: 40px;

            h3 {
              font-size: 15px;
              font-weight: 500;
            }

            > div {
              display: flex;
              flex-wrap: wrap;
              justify-content: center;
              margin-top: 15px;

              > div {
                padding: 3px 6px;
                margin: 0 10px 10px 0;
                font-size: 12px;
                background: var(--art-main-bg-color);
                border: 1px solid var(--art-border-color);
                border-radius: 2px;
              }
            }
          }
        }

        .gallery {
          margin-top: 25px;
          border-radius: 10px;

          .item {
            img {
              width: 100%;
              height: 100%;
              object-fit: cover;
            }
          }
        }
      }

      .right-wrap {
        flex: 1;
        overflow: hidden;
        border-radius: $box-radius;

        .info {
          background: var(--art-main-bg-color);
          border-radius: $box-radius;

          .title {
            padding: 15px 25px;
            font-size: 20px;
            font-weight: 400;
            color: var(--art-text-gray-800);
            border-bottom: 1px solid var(--art-border-color);
          }

          .form {
            box-sizing: border-box;
            padding: 30px 25px;

            > .el-row {
              .el-form-item {
                width: calc(50% - 10px);
              }

              .el-input,
              .el-select {
                width: 100%;
              }
            }

            .right-input {
              margin-left: 20px;
            }

            .el-form-item-right {
              display: flex;
              align-items: center;
              justify-content: end;

              .el-button {
                width: 110px !important;
              }
            }
          }
        }
      }
    }
  }

  @media only screen and (max-width: $device-ipad-vertical) {
    .page-content {
      .content {
        display: block;
        margin-top: 5px;

        .left-wrap {
          width: 100%;
        }

        .right-wrap {
          width: 100%;
          margin-top: 15px;
        }
      }
    }
  }
</style>
