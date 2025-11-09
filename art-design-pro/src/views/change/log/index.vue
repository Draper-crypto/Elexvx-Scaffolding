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
        <ElTableColumn prop="summary" label="更新摘要" min-width="220">
          <template #default="{ row }">
            <span v-if="row.summary" v-html="renderRichText(row.summary)"></span>
            <span v-else>-</span>
          </template>
        </ElTableColumn>
        <ElTableColumn label="备注" prop="remark" min-width="180" />
        <ElTableColumn label="更新内容" width="120">
          <template #default="{ row }">
            <ElButton type="primary" link @click="openDetail(row)">查看详情</ElButton>
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
        <ElFormItem label="更新摘要" prop="summary">
          <ElInput v-model.trim="formModel.summary" placeholder="请输入简单描述" />
        </ElFormItem>
        <ElFormItem label="发布日期" prop="releaseDate">
          <ElDatePicker
            v-model="formModel.releaseDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择日期"
          />
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

    <ElDrawer
      v-model="detailVisible"
      title="更新详情"
      size="45%"
      destroy-on-close
    >
      <div v-if="detailRecord">
        <ElDescriptions :column="1" border>
          <ElDescriptionsItem label="版本号">{{ detailRecord.version }}</ElDescriptionsItem>
          <ElDescriptionsItem label="标题">{{ detailRecord.title }}</ElDescriptionsItem>
        <ElDescriptionsItem label="更新摘要">
            <div v-if="detailRecord.summary" v-html="renderRichText(detailRecord.summary)"></div>
            <span v-else>-</span>
        </ElDescriptionsItem>
          <ElDescriptionsItem label="发布日期">
            {{ detailRecord.releaseDate ? dayjs(detailRecord.releaseDate).format('YYYY-MM-DD') : '-' }}
          </ElDescriptionsItem>
          <ElDescriptionsItem label="备注">{{ detailRecord.remark || '-' }}</ElDescriptionsItem>
        </ElDescriptions>
        <div class="detail-content" v-html="renderRichText(detailRecord.content)"></div>
      </div>
      <div v-else class="detail-placeholder">
        <ElEmpty description="暂无内容" />
      </div>
    </ElDrawer>
  </div>
</template>

<script setup lang="ts">
  import '@wangeditor/editor/dist/css/style.css'
  import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref, shallowRef } from 'vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
  import { IDomEditor, IToolbarConfig } from '@wangeditor/editor'
  import MarkdownIt from 'markdown-it'
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
  const detailVisible = ref(false)
  const detailRecord = ref<Api.ChangeLog.Item | null>(null)
  const markdownParser = new MarkdownIt()
  const htmlTagPattern = /<[a-z][\s\S]*?>/i

  const renderRichText = (value?: string): string => {
    if (!value) return ''
    if (htmlTagPattern.test(value)) {
      return value
    }
    return markdownParser.render(value)
  }

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
    summary: '',
    releaseDate: '',
    remark: ''
  })

  const rules: FormRules = {
    version: [{ required: true, message: '请输入版本号', trigger: 'blur' }],
    title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
    summary: [{ required: true, message: '请输入更新摘要', trigger: 'blur' }],
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
        summary: row.summary || '',
        releaseDate: row.releaseDate || '',
        remark: row.remark || ''
      })
    } else {
      Object.assign(formModel, {
        id: undefined,
        version: '',
        title: '',
        content: '',
        summary: '',
        releaseDate: dayjs().format('YYYY-MM-DD'),
        remark: ''
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
          summary: formModel.summary,
          releaseDate: formModel.releaseDate,
          remark: formModel.remark
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

  const openDetail = (row: Api.ChangeLog.Item) => {
    detailRecord.value = row
    detailVisible.value = true
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

    .detail-content {
      margin-top: 16px;
      padding: 16px;
      border: 1px solid var(--el-border-color);
      border-radius: 6px;
      color: var(--art-gray-700);
      font-size: 14px;
      line-height: 1.6;
      background-color: var(--el-color-white);
    }

    .detail-placeholder {
      padding: 24px 0;
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
