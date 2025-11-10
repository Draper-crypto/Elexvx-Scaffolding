<template>
  <div class="operation-log-page art-full-height">
    <ElCard class="search-card" shadow="never">
      <ElForm :inline="true" :model="searchForm">
        <ElFormItem label="操作用户">
          <ElInput v-model.trim="searchForm.username" placeholder="支持模糊搜索" clearable />
        </ElFormItem>
        <ElFormItem label="操作类型">
          <ElSelect v-model="searchForm.actionType" placeholder="全部类型" clearable style="width: 180px">
            <ElOption label="全部类型" value="" />
            <ElOption
              v-for="item in operationTypes"
              :key="item.actionType"
              :label="item.label"
              :value="item.actionType"
            />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="执行结果">
          <ElSelect v-model="searchForm.success" placeholder="全部" style="width: 150px">
            <ElOption label="全部" value="" />
            <ElOption label="成功" :value="true" />
            <ElOption label="失败" :value="false" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem>
          <ElButton type="primary" @click="handleSearch" :loading="loading">查询</ElButton>
          <ElButton @click="handleReset" :disabled="loading">重置</ElButton>
        </ElFormItem>
      </ElForm>
    </ElCard>

    <ElCard class="art-table-card" shadow="never">
      <ArtTableHeader
        v-model:columns="columnChecks"
        :loading="loading"
        @refresh="refreshData"
      />
      <ArtTable
        :loading="loading"
        :data="data"
        :columns="columns"
        :pagination="pagination"
        @pagination:size-change="handleSizeChange"
        @pagination:current-change="handleCurrentChange"
      />
    </ElCard>
  </div>
</template>

<script setup lang="ts">
  import { fetchOperationLogList, fetchOperationLogTypes } from '@/api/operation-log'
  import { useTable } from '@/composables/useTable'
  import { ElTag, ElTooltip } from 'element-plus'
  import { h, onMounted, reactive, ref } from 'vue'

  defineOptions({ name: 'SystemOperationLog' })

  const operationTypes = ref<Api.OperationLog.TypeMeta[]>([])

  const defaultSearchState = () => ({
    username: '',
    actionType: '',
    success: '' as '' | boolean
  })

  const searchForm = reactive(defaultSearchState())

  const {
    columns,
    columnChecks,
    data,
    loading,
    pagination,
    getData,
    searchParams,
    handleSizeChange,
    handleCurrentChange,
    refreshData
  } = useTable({
    core: {
      apiFn: fetchOperationLogList,
      apiParams: {
        current: 1,
        size: 20
      },
      immediate: false,
      columnsFactory: () => [
        { type: 'index', width: 60, label: '序号' },
        {
          prop: 'actionSummary',
          label: '操作内容',
          minWidth: 240,
          formatter: (row: Api.OperationLog.Item) => {
            const detail = row.actionDetail && row.actionDetail !== row.actionSummary ? row.actionDetail : ''
            if (detail) {
              return h(
                'div',
                {
                  class: 'log-summary'
                },
                [
                  h('div', { class: 'log-summary__title' }, row.actionSummary),
                  h(
                    ElTooltip,
                    { content: detail, placement: 'top', effect: 'dark' },
                    {
                      default: () => h('span', { class: 'log-summary__detail' }, '查看详情')
                    }
                  )
                ]
              )
            }
            return row.actionSummary
          }
        },
        {
          prop: 'actionType',
          label: '类型',
          width: 140,
          formatter: (row: Api.OperationLog.Item) =>
            h(
              ElTag,
              {
                type: normalizeTagType(row.tagType),
                effect: 'light',
                style: row.colorHex ? { color: row.colorHex, borderColor: row.colorHex } : undefined
              },
              () => row.actionTypeLabel
            )
        },
        {
          prop: 'username',
          label: '操作用户',
          minWidth: 140,
          formatter: (row: Api.OperationLog.Item) => row.username || '系统'
        },
        {
          prop: 'requestUri',
          label: '请求信息',
          minWidth: 220,
          formatter: (row: Api.OperationLog.Item) =>
            h('div', { class: 'log-request' }, [
              row.requestMethod
                ? h(
                    ElTag,
                    { size: 'small', type: methodTagType(row.requestMethod) },
                    () => row.requestMethod
                  )
                : null,
              h('span', { class: 'log-request__uri' }, row.requestUri || '-')
            ])
        },
        {
          prop: 'ipAddress',
          label: 'IP 地址',
          width: 140,
          formatter: (row: Api.OperationLog.Item) => row.ipAddress || '-'
        },
        {
          prop: 'success',
          label: '执行结果',
          width: 120,
          formatter: (row: Api.OperationLog.Item) => {
            const tag = h(
              ElTag,
              { type: row.success ? 'success' : 'danger' },
              () => (row.success ? '成功' : '失败')
            )
            if (!row.success && row.errorMessage) {
              return h(
                ElTooltip,
                { content: row.errorMessage, placement: 'top', effect: 'dark' },
                { default: () => tag }
              )
            }
            return tag
          }
        },
        {
          prop: 'createdAt',
          label: '发生时间',
          width: 180,
          sortable: true,
          formatter: (row: Api.OperationLog.Item) => row.createdAt || '-'
        }
      ]
    }
  })

  const normalizeTagType = (tag?: string) => {
    const allowed = ['success', 'warning', 'danger', 'info']
    if (tag && allowed.includes(tag)) {
      return tag as 'success' | 'warning' | 'danger' | 'info'
    }
    return 'info'
  }

  const methodTagType = (method?: string) => {
    const upper = (method || '').toUpperCase()
    if (upper === 'GET') return 'info'
    if (upper === 'POST') return 'success'
    if (upper === 'PUT' || upper === 'PATCH') return 'warning'
    if (upper === 'DELETE') return 'danger'
    return 'info'
  }

  const applySearchFilters = () => {
    searchParams.current = 1
    searchParams.username = searchForm.username || undefined
    searchParams.actionType = searchForm.actionType || undefined
    searchParams.success = searchForm.success === '' ? undefined : searchForm.success
  }

  const handleSearch = () => {
    applySearchFilters()
    getData()
  }

  const handleReset = () => {
    Object.assign(searchForm, defaultSearchState())
    applySearchFilters()
    getData()
  }

  const loadTypeMetadata = async () => {
    try {
      operationTypes.value = await fetchOperationLogTypes()
    } catch (error) {
      console.warn('加载操作类型失败', error)
    }
  }

  onMounted(async () => {
    await loadTypeMetadata()
    handleReset()
  })
</script>

<style scoped>
  .operation-log-page {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .search-card {
    flex: 0 0 auto;
  }

  .log-summary {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  .log-summary__title {
    font-weight: 500;
  }

  .log-summary__detail {
    color: var(--el-color-primary);
    cursor: pointer;
    font-size: 12px;
  }

  .log-request {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .log-request__uri {
    color: var(--el-text-color-primary);
  }
</style>
