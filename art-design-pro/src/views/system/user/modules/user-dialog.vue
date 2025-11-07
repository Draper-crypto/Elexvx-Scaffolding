<template>
  <ElDialog
    v-model="dialogVisible"
    :title="dialogType === 'add' ? '添加用户' : '编辑用户'"
    width="30%"
    align-center
  >
    <ElForm ref="formRef" :model="formData" :rules="rules" label-width="80px">
      <ElFormItem label="用户名" prop="username">
        <ElInput v-model="formData.username" placeholder="请输入用户名" />
      </ElFormItem>
      <ElFormItem v-if="dialogType === 'add'" label="密码" prop="password">
        <ElInput v-model="formData.password" type="password" placeholder="请输入密码" />
      </ElFormItem>
      <ElFormItem label="手机号" prop="phone">
        <ElInput v-model="formData.phone" placeholder="请输入手机号" />
      </ElFormItem>
      <ElFormItem label="性别" prop="gender">
        <ElSelect v-model="formData.gender">
          <ElOption label="未知" :value="0" />
          <ElOption label="男" :value="1" />
          <ElOption label="女" :value="2" />
        </ElSelect>
      </ElFormItem>
      <ElFormItem label="角色" prop="roleIds">
        <ElSelect v-model="formData.roleIds" multiple>
          <ElOption
            v-for="role in roleList"
            :key="role.roleId"
            :value="role.roleId"
            :label="role.roleName"
          />
        </ElSelect>
      </ElFormItem>
    </ElForm>
    <template #footer>
      <div class="dialog-footer">
        <ElButton @click="dialogVisible = false">取消</ElButton>
        <ElButton type="primary" @click="handleSubmit">提交</ElButton>
      </div>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import { fetchGetRoleList, createUser, updateUser } from '@/api/system-manage'
  import type { FormInstance, FormRules } from 'element-plus'

  interface Props {
    visible: boolean
    type: string
    userData?: Partial<Api.SystemManage.UserListItem>
  }

  interface Emits {
    (e: 'update:visible', value: boolean): void
    (e: 'submit'): void
  }

  const props = defineProps<Props>()
  const emit = defineEmits<Emits>()

  // 角色列表数据（从后端拉取）
  const roleList = ref<Api.SystemManage.RoleListItem[]>([])

  // 对话框显示控制
  const dialogVisible = computed({
    get: () => props.visible,
    set: (value) => emit('update:visible', value)
  })

  const dialogType = computed(() => props.type)

  // 表单实例
  const formRef = ref<FormInstance>()

  // 表单数据
  const formData = reactive({
    username: '',
    password: '',
    phone: '',
    gender: 0 as number, // 0未知 1男 2女
    roleIds: [] as number[]
  })

  // 表单验证规则
  const rules: FormRules = {
    username: [
      { required: true, message: '请输入用户名', trigger: 'blur' },
      { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
    ],
    password: [
      {
        required: computed(() => dialogType.value === 'add'),
        message: '请输入密码',
        trigger: 'blur'
      },
      { min: 6, max: 128, message: '密码长度为 6-128 位', trigger: 'blur' }
    ],
    phone: [
      { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
    ],
    gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
    roleIds: []
  }

  /**
   * 初始化表单数据
   * 根据对话框类型（新增/编辑）填充表单
   */
  const initFormData = () => {
    const isEdit = props.type === 'edit' && props.userData
    const row = props.userData

    Object.assign(formData, {
      username: isEdit && row ? row.userName || '' : '',
      password: '',
      phone: isEdit && row ? row.userPhone || '' : '',
      gender: isEdit && row ? mapGenderToNumber(row.userGender) : 0,
      roleIds: isEdit && row ? (Array.isArray(row.userRoles) ? row.userRoles.map(mapRoleCodeToId) : []) : []
    })
  }

  // 性别映射：字符串 -> 数字
  function mapGenderToNumber(g?: string) {
    if (g === '男') return 1
    if (g === '女') return 2
    return 0
  }

  // 角色 code -> id 映射（根据当前角色列表）
  function mapRoleCodeToId(code: string) {
    const found = roleList.value.find((r) => r.roleCode === code)
    return found ? found.roleId : 0
  }

  /**
   * 监听对话框状态变化
   * 当对话框打开时初始化表单数据并清除验证状态
   */
  watch(
    () => [props.visible, props.type, props.userData],
    async ([visible]) => {
      if (visible) {
        // 加载角色列表
        try {
          const res = await fetchGetRoleList({ current: 1, size: 100 })
          roleList.value = Array.isArray(res.records) ? res.records : []
        } catch {}

        initFormData()
        nextTick(() => {
          formRef.value?.clearValidate()
        })
      }
    },
    { immediate: true }
  )

  /**
   * 提交表单
   * 验证通过后触发提交事件
   */
  const handleSubmit = async () => {
    if (!formRef.value) return

    await formRef.value.validate(async (valid) => {
      if (!valid) return

      try {
        if (dialogType.value === 'add') {
          await createUser({
            username: formData.username,
            password: formData.password,
            phone: formData.phone,
            gender: formData.gender,
            roleIds: formData.roleIds
          })
          ElMessage.success('添加成功')
        } else {
          const id = props.userData?.id as number
          await updateUser(id, {
            phone: formData.phone,
            gender: formData.gender
          })
          ElMessage.success('更新成功')
        }
        dialogVisible.value = false
        emit('submit')
      } catch (error) {
        // 错误已由请求模块统一处理
      }
    })
  }
</script>
