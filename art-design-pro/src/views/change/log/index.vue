<template>
  <div class="page-content changelog-page">
    <ElCard shadow="never" class="changelog-card">
      <div class="toolbar" v-if="canManage">
        <ElInput
          v-model.trim="searchForm.keyword"
          placeholder="搜索版本号或标题"
          clearable
          class="keyword-input"
          @keyup.enter="loadLogs"
        >
          <template #prefix>
            <i class="iconfont-sys">&#xe710;</i>
          </template>
        </ElInput>
        <ElDatePicker
          v-model="searchForm.releaseDate"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="发布日期"
          class="date-input"
          clearable
        />
        <ElButton type="primary" @click="loadLogs" v-ripple>查询</ElButton>
        <ElButton @click="resetFilters" v-ripple>重置</ElButton>
        <ElButton type="success" @click="openForm()" v-ripple>新增日志</ElButton>
      </div>

      <ElTable :data="logList" v-loading="loading" row-key="id" border>
        <ElTableColumn prop="version" label="版本号" width="120" />
        <ElTableColumn prop="title" label="标题" min-width="200" />
        <ElTableColumn prop="releaseDate" label="发布日期" width="140">
          <template #default="{ row }">
            {{ row.releaseDate ? dayjs(row.releaseDate).format('YYYY-MM-DD') : '-' }}
          </template>
        </ElTableColumn>
        <ElTableColumn prop="requireReLogin" label="需重登" width="100">
          <template #default="{ row }">
            <ElTag :type="row.requireReLogin ? 'danger' : 'info'" size="small">
              {{ row.requireReLogin ? '是' : '否' }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn label="备注" prop="remark" min-width="180" />
        <ElTableColumn label="内容预览" min-width="220">
          <template #default="{ row }">
            <div class="content-preview" v-html="row.content"></div>
          </template>
        </ElTableColumn>
        <ElTableColumn v-if="canManage" label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <ElSpace>
              <ElButton size="small" @click="openForm(row)" v-ripple>编辑</ElButton>
              <ElButton size="small" type="danger" @click="handleDelete(row)" v-ripple>
                删除
              </ElButton>
            </ElSpace>
          </template>
        </ElTableColumn>
      </ElTable>

      <div class="pagination" v-if="canManage">
        <ElPagination
          background
          layout="prev, pager, next, jumper"
          :total="pagination.total"
          :current-page="pagination.current"
          :page-size="pagination.size"
          @current-change="handlePageChange"
        />
      </div>
    </ElCard>

    <ElDrawer
      v-if="canManage"
      v-model="formVisible"
      :title="formModel.id ? '编辑更新日志' : '新增更新日志'"
      size="50%"
      destroy-on-close
    >
      <ElForm ref="formRef" :model="formModel" :rules="rules" label-width="100px">
        <ElFormItem label="版本号" prop="version">
          <ElInput v-model.trim="formModel.version" placeholder="例如 v2.6.1" />
        </ElFormItem>
        <ElFormItem label="标题" prop="title">
          <ElInput v-model.trim="formModel.title" placeholder="更新标题" />
        </ElFormItem>
        <ElFormItem label="发布日期" prop="releaseDate">
          <ElDatePicker
            v-model="formModel.releaseDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择日期"
          />
        </ElFormItem>
        <ElFormItem label="需重登" prop="requireReLogin">
          <ElSwitch v-model="formModel.requireReLogin" />
        </ElFormItem>
        <ElFormItem label="备注">
          <ElInput v-model.trim="formModel.remark" placeholder="可选备注" />
        </ElFormItem>
        <ElFormItem label="更新内容" prop="content">
          <div class="editor-wrapper">
            <Toolbar class="toolbar" :editor="editorRef" :defaultConfig="toolbarConfig" />
            <Editor
              v-model="formModel.content"
              class="editor"
              :defaultConfig="editorConfig"
              @onCreated="handleEditorCreated"
            />
          </div>
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElSpace>
          <ElButton @click="formVisible = false">取消</ElButton>
          <ElButton type="primary" @click="handleSubmit" :loading="formSubmitting">保存</ElButton>
        </ElSpace>
      </template>
    </ElDrawer>
  </div>
</template>

<script setup lang="ts">
  import '@wangeditor/editor/dist/css/style.css'
  import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref, shallowRef } from 'vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
  import { IDomEditor, IToolbarConfig } from '@wangeditor/editor'
  import type { FormInstance, FormRules } from 'element-plus'
  import dayjs from 'dayjs'
  import { fetchChangeLogs, fetchCreateChangeLog, fetchDeleteChangeLog, fetchPublicChangeLogs, fetchUpdateChangeLog } from '@/api/change-log'
  import { useUserStore } from '@/store/modules/user'

  defineOptions({ name: 'ChangeLog' })

  const userStore = useUserStore()
  const canManage = computed(() => (userStore.getUserInfo.roles || []).includes('R_SUPER'))

  const loading = ref(false)
  const formSubmitting = ref(false)
  const formVisible = ref(false)
  const logList = ref<Api.ChangeLog.Item[]>([])
  const formRef = ref<FormInstance>()
  const editorRef = shallowRef<IDomEditor>()

  const pagination = reactive({
    current: 1,
    size: 10,
    total: 0
  })

  const searchForm = reactive({
    keyword: '',
    releaseDate: ''
  })

  const formModel = reactive<Api.ChangeLog.FormData & { id?: number }>({
    id: undefined,
    version: '',
    title: '',
    content: '',
    releaseDate: '',
    remark: '',
    requireReLogin: false
  })

  const rules: FormRules = {
    version: [{ required: true, message: '请输入版本号', trigger: 'blur' }],
    title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
    content: [{ required: true, message: '请输入更新内容', trigger: 'blur' }]
  }

  const toolbarConfig: Partial<IToolbarConfig> = {
    excludeKeys: ['todo']
  }

  const editorConfig = {
    placeholder: '请输入更新内容'
  }

  const loadLogs = async () => {
    loading.value = true
    try {
      if (canManage.value) {
        const params: any = {
          page: pagination.current,
          size: pagination.size,
          keyword: searchForm.keyword
        }
        if (searchForm.releaseDate) {
          params.releaseDate = searchForm.releaseDate
        }
        const res = await fetchChangeLogs(params)
        logList.value = res.records
        pagination.total = res.total
        pagination.current = res.current
        pagination.size = res.size
      } else {
        const res = await fetchPublicChangeLogs()
        logList.value = res
        pagination.total = res.length
      }
    } finally {
      loading.value = false
    }
  }

  const resetFilters = () => {
    searchForm.keyword = ''
    searchForm.releaseDate = ''
    pagination.current = 1
    loadLogs()
  }

  const handlePageChange = (page: number) => {
    pagination.current = page
    loadLogs()
  }

  const openForm = (row?: Api.ChangeLog.Item) => {
    if (!canManage.value) return
    if (row) {
      Object.assign(formModel, {
        id: row.id,
        version: row.version,
        title: row.title,
        content: row.content,
        releaseDate: row.releaseDate || '',
        remark: row.remark || '',
        requireReLogin: !!row.requireReLogin
      })
    } else {
      Object.assign(formModel, {
        id: undefined,
        version: '',
        title: '',
        content: '',
        releaseDate: dayjs().format('YYYY-MM-DD'),
        remark: '',
        requireReLogin: false
      })
    }
    formVisible.value = true
    nextTick(() => formRef.value?.clearValidate())
  }

  const handleSubmit = async () => {
    if (!formRef.value || !canManage.value) return
    await formRef.value.validate(async (valid) => {
      if (!valid) return
      formSubmitting.value = true
      try {
        const payload: Api.ChangeLog.FormData = {
          version: formModel.version,
          title: formModel.title,
          content: formModel.content,
          releaseDate: formModel.releaseDate,
          remark: formModel.remark,
          requireReLogin: formModel.requireReLogin
        }
        if (formModel.id) {
          await fetchUpdateChangeLog(formModel.id, payload)
          ElMessage.success('更新成功')
        } else {
          await fetchCreateChangeLog(payload)
          ElMessage.success('创建成功')
        }
        formVisible.value = false
        loadLogs()
      } finally {
        formSubmitting.value = false
      }
    })
  }

  const handleDelete = (row: Api.ChangeLog.Item) => {
    if (!canManage.value) return
    ElMessageBox.confirm(`确定删除版本 ${row.version} 的日志吗？`, '提示', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      await fetchDeleteChangeLog(row.id)
      ElMessage.success('删除成功')
      if (logList.value.length === 1 && pagination.current > 1) {
        pagination.current -= 1
      }
      loadLogs()
    })
  }

  const handleEditorCreated = (editor: IDomEditor) => {
    editorRef.value = editor
  }

  onMounted(() => {
    loadLogs()
  })

  onBeforeUnmount(() => {
    if (editorRef.value) {
      editorRef.value.destroy()
    }
  })
</script>

<style lang="scss" scoped>
  .changelog-page {
    .toolbar {
      display: flex;
      flex-wrap: wrap;
      gap: 12px;
      margin-bottom: 20px;

      .keyword-input,
      .date-input {
        width: 220px;
      }
    }

    .content-preview {
      max-height: 70px;
      overflow: hidden;
      color: var(--art-gray-700);
      font-size: 13px;
      line-height: 1.5;
    }

    .editor-wrapper {
      width: 100%;
      border: 1px solid var(--el-border-color);
      border-radius: 6px;

      .toolbar {
        border-bottom: 1px solid var(--el-border-color);
      }

      .editor {
        height: 260px;
        overflow-y: auto;
      }
    }

    .pagination {
      display: flex;
      justify-content: flex-end;
      margin-top: 20px;
    }
  }
</style>
