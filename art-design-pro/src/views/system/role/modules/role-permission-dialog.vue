<template>
  <ElDialog
    v-model="visible"
    title="菜单权限"
    width="520px"
    align-center
    class="el-dialog-border"
    @close="handleClose"
  >
    <ElScrollbar height="70vh">
      <template v-if="roleId">
        <div v-if="!treeData.length && !loading" class="empty-holder">
          <ElEmpty description="暂无菜单数据" :image-size="120" />
        </div>
        <ElTree
          v-else
          ref="treeRef"
          :data="treeData"
          show-checkbox
          node-key="nodeKey"
          :default-expand-all="isExpandAll"
          :props="defaultProps"
          :expand-on-click-node="false"
          v-loading="loading"
          @check="handleTreeCheck"
        >
          <template #default="{ data }">
            <div class="tree-node">
              <span class="node-label">{{ data.label }}</span>
              <ElTag v-if="data.isAuth" type="info" size="small" effect="plain">按钮</ElTag>
            </div>
          </template>
        </ElTree>
      </template>
      <div v-else class="empty-holder">
        <ElEmpty description="请选择角色" :image-size="120" />
      </div>
    </ElScrollbar>
    <template #footer>
      <div class="dialog-footer">
        <ElButton @click="outputSelectedData" style="margin-left: 8px">获取选中数据</ElButton>

        <ElButton @click="toggleExpandAll">{{ isExpandAll ? '全部收起' : '全部展开' }}</ElButton>
        <ElButton @click="toggleSelectAll" style="margin-left: 8px">{{
          isSelectAll ? '取消全选' : '全部选择'
        }}</ElButton>
        <ElButton type="primary" @click="savePermission">保存</ElButton>
      </div>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import { computed, nextTick, ref, watch } from 'vue'
  import { ElMessage } from 'element-plus'
  import { formatMenuTitle } from '@/router/utils/utils'
  import type { AppRouteRecord } from '@/types/router'
  import {
    fetchGetMenuList,
    fetchGetRoleMenuIds,
    fetchGetRolePermissionIds,
    fetchSetRoleMenus,
    fetchSetRolePermissions
  } from '@/api/system-manage'

  type RoleListItem = Api.SystemManage.RoleListItem

  interface Props {
    modelValue: boolean
    roleData?: RoleListItem
  }

  interface Emits {
    (e: 'update:modelValue', value: boolean): void
    (e: 'success'): void
  }

  interface MenuNode {
    nodeKey: string
    label: string
    isAuth?: boolean
    menuId?: number
    permissionId?: number
    children?: MenuNode[]
  }

  const props = withDefaults(defineProps<Props>(), {
    modelValue: false,
    roleData: undefined
  })

  const emit = defineEmits<Emits>()

  const visible = computed({
    get: () => props.modelValue,
    set: (value) => emit('update:modelValue', value)
  })

  const roleId = computed(() => props.roleData?.roleId ?? null)

  const treeRef = ref()
  const isExpandAll = ref(true)
  const isSelectAll = ref(false)
  const loading = ref(false)
  const treeData = ref<MenuNode[]>([])

  const defaultProps = {
    children: 'children',
    label: (data: MenuNode) => data.label || ''
  }

  watch(
    () => props.modelValue,
    (open) => {
      if (open && roleId.value) {
        loadRolePermissions(roleId.value)
      }
      if (!open) {
        resetTreeState()
      }
    }
  )

  watch(
    () => roleId.value,
    (newRoleId, oldRoleId) => {
      if (visible.value && newRoleId && newRoleId !== oldRoleId) {
        loadRolePermissions(newRoleId)
      }
    }
  )

  const resetTreeState = () => {
    treeRef.value?.setCheckedKeys([])
    treeData.value = []
    isSelectAll.value = false
  }

  const loadRolePermissions = async (id: number) => {
    loading.value = true
    try {
      const [menus, menuIds, permissionIds] = await Promise.all([
        fetchGetMenuList('admin'),
        fetchGetRoleMenuIds(id),
        fetchGetRolePermissionIds(id)
      ])
      treeData.value = buildTreeNodes(menus || [])
      await nextTick()
      const defaultKeys = [
        ...(menuIds || []).map((mid) => `menu-${mid}`),
        ...(permissionIds || []).map((pid) => `perm-${pid}`)
      ]
      treeRef.value?.setCheckedKeys(defaultKeys)
      updateSelectAllState()
    } catch (error) {
      console.error('加载菜单权限失败', error)
      treeData.value = []
    } finally {
      loading.value = false
    }
  }

  const buildTreeNodes = (menus: AppRouteRecord[]): MenuNode[] => {
    return (menus || []).map((menu) => {
      const node: MenuNode = {
        nodeKey:
          menu.id != null ? `menu-${menu.id}` : `menu-${menu.path || Math.random().toString(36).slice(2)}`,
        label: formatMenuTitle(menu.meta?.title || menu.name || ''),
        menuId: menu.id != null ? Number(menu.id) : undefined,
        children: []
      }
      if (menu.children?.length) {
        node.children = buildTreeNodes(menu.children)
      }
      if (menu.meta?.authList?.length) {
        const authNodes: MenuNode[] = menu.meta.authList.map((auth) => ({
          nodeKey:
            auth.id != null
              ? `perm-${auth.id}`
              : `perm-${menu.id ?? menu.path ?? 'temp'}-${auth.authMark}`,
          label: auth.title || auth.authMark,
          isAuth: true,
          permissionId: auth.id != null ? Number(auth.id) : undefined,
          menuId: auth.menuId
        }))
        node.children =
          node.children && node.children.length ? [...node.children, ...authNodes] : authNodes
      }
      return node
    })
  }

  const getAllNodeKeys = (nodes: MenuNode[]): string[] => {
    const keys: string[] = []
    nodes.forEach((node) => {
      keys.push(node.nodeKey)
      if (node.children?.length) {
        keys.push(...getAllNodeKeys(node.children))
      }
    })
    return keys
  }

  const updateSelectAllState = () => {
    const tree = treeRef.value
    if (!tree) {
      isSelectAll.value = false
      return
    }
    const checkedKeys = tree.getCheckedKeys()
    const allKeys = getAllNodeKeys(treeData.value)
    isSelectAll.value = allKeys.length > 0 && checkedKeys.length === allKeys.length
  }

  const toggleExpandAll = () => {
    const tree = treeRef.value
    if (!tree) return

    const nodes = tree.store.nodesMap
    Object.values(nodes).forEach((node: any) => {
      node.expanded = !isExpandAll.value
    })

    isExpandAll.value = !isExpandAll.value
  }

  const toggleSelectAll = () => {
    const tree = treeRef.value
    if (!tree) return

    if (!isSelectAll.value) {
      const allKeys = getAllNodeKeys(treeData.value)
      tree.setCheckedKeys(allKeys)
    } else {
      tree.setCheckedKeys([])
    }

    isSelectAll.value = !isSelectAll.value
  }

  const handleTreeCheck = () => {
    updateSelectAllState()
  }

  const buildSelectionPayload = () => {
    const tree = treeRef.value
    if (!tree) return { menuIds: [] as number[], permissionIds: [] as number[] }

    const checkedKeys: string[] = tree.getCheckedKeys()
    const halfCheckedKeys: string[] = tree.getHalfCheckedKeys()

    const menuIds = [...checkedKeys, ...halfCheckedKeys]
      .filter((key) => key.startsWith('menu-'))
      .map((key) => Number(key.replace('menu-', '')))
    const permissionIds = checkedKeys
      .filter((key) => key.startsWith('perm-'))
      .map((key) => Number(key.replace('perm-', '')))

    return {
      menuIds: Array.from(new Set(menuIds)),
      permissionIds: Array.from(new Set(permissionIds))
    }
  }

  const savePermission = async () => {
    if (!roleId.value) {
      ElMessage.warning('请选择角色')
      return
    }
    const { menuIds, permissionIds } = buildSelectionPayload()
    try {
      await Promise.all([
        fetchSetRoleMenus(roleId.value, menuIds),
        fetchSetRolePermissions(roleId.value, permissionIds)
      ])
      ElMessage.success('权限保存成功')
      emit('success')
      handleClose()
    } catch (error) {
      console.error('保存权限失败', error)
    }
  }

  const handleClose = () => {
    visible.value = false
    resetTreeState()
  }

  const outputSelectedData = () => {
    const tree = treeRef.value
    if (!tree) return

    const payload = buildSelectionPayload()
    const selectedData = {
      ...payload,
      checkedKeys: tree.getCheckedKeys(),
      halfCheckedKeys: tree.getHalfCheckedKeys()
    }

    console.log('=== 选中的权限数�?===', selectedData)
    ElMessage.success(`已输出选中数据到控制台，共选中 ${payload.permissionIds.length} 个按钮节点`)
  }
</script>


<style lang="scss" scoped>
  .dialog-footer {
    display: flex;
    gap: 12px;
    justify-content: flex-end;
  }

  .empty-holder {
    padding: 24px 0;
  }

  .tree-node {
    display: flex;
    align-items: center;
    gap: 6px;

    .node-label {
      flex: 1;
    }
  }
</style>

