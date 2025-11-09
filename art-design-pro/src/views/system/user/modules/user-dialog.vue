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
      <ElFormItem label="手机号" prop="phone">
        <ElInput v-model="formData.phone" placeholder="请输入手机号" />
      </ElFormItem>
      <ElFormItem label="性别" prop="gender">
        <ElSelect v-model="formData.gender" placeholder="请选择性别">
          <ElOption
            v-for="item in genderOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </ElSelect>
      </ElFormItem>
      <ElFormItem label="角色" prop="roleIds">
        <ElSelect v-model="formData.roleIds" multiple>
          <ElOption
            v-for="role in roleList"
            :key="role.id"
            :value="role.id"
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
import { computed, nextTick, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { fetchGetRoleList } from '@/api/system-manage'
import type { FormInstance, FormRules } from 'element-plus'

type DialogType = 'add' | 'edit'

interface Props {
  visible: boolean
  type: DialogType
  userData?: Partial<Api.SystemManage.UserListItem>
}

interface Emits {
  (e: 'update:visible', value: boolean): void
  (e: 'submit', payload: any): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const genderOptions = [
  { label: '男', value: '1' },
  { label: '女', value: '2' }
]

const roleList = ref<any[]>([])

const dialogVisible = computed({
  get: () => props.visible,
  set: (value) => emit('update:visible', value)
})

const dialogType = computed(() => props.type)

const formRef = ref<FormInstance>()

const formData = reactive({
  username: '',
  phone: '',
  gender: '',
  roleIds: [] as number[]
})

const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在 2 到 20 之间', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入合法手机号', trigger: 'blur' }
  ],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  roleIds: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const initFormData = () => {
  const isEdit = props.type === 'edit' && props.userData
  const row = props.userData

  Object.assign(formData, {
    username: isEdit && row ? row.userName || '' : '',
    phone: isEdit && row ? row.userPhone || '' : '',
    gender: isEdit && row ? String(row.userGender ?? '') : '',
    roleIds: [] as number[]
  })

  if (isEdit && row && Array.isArray(row.userRoles) && roleList.value.length) {
    formData.roleIds = row.userRoles
      .map((code: string) => roleList.value.find((r: any) => r.roleCode === code)?.id)
      .filter((id: any) => typeof id === 'number') as number[]
  }
}

watch(
  () => [props.visible, props.type, props.userData],
  ([visible]) => {
    if (visible) {
      initFormData()
      nextTick(() => {
        formRef.value?.clearValidate()
      })
    }
  },
  { immediate: true }
)

onMounted(async () => {
  try {
    const res: any = await fetchGetRoleList({ current: 1, size: 100 })
    roleList.value = Array.isArray(res.records) ? res.records : []
  } catch (error) {
    console.error('[UserDialog] 加载角色失败', error)
  }
})

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate((valid) => {
    if (!valid) return

    const payload = {
      username: formData.username,
      phone: formData.phone,
      gender: Number(formData.gender || 0),
      roleIds: Array.from(new Set(formData.roleIds))
    }
    ElMessage.success(dialogType.value === 'add' ? '添加成功' : '更新成功')
    dialogVisible.value = false
    emit('submit', payload)
  })
}
</script>

<style lang="scss" scoped>
.dialog-footer {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}
</style>
