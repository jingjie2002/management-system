<template>
  <el-card shadow="hover">
    <template #header>
      <div class="header">
        <span>用户管理</span>
        <el-button type="primary" @click="loadUsers">刷新</el-button>
      </div>
    </template>

    <el-table :data="users" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="90" />
      <el-table-column prop="username" label="用户名" width="160" />
      <el-table-column prop="realName" label="姓名" width="180" />
      <el-table-column prop="phone" label="电话" width="150" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="status" label="状态" width="120">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="scope">
          <el-button size="small" :type="scope.row.status === 1 ? 'danger' : 'success'" @click="handleStatusChange(scope.row)">
            {{ scope.row.status === 1 ? '禁用' : '启用' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue'
import request from '../../api/http'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const users = ref<any[]>([])

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await request.get('/users')
    users.value = res.data || []
  } finally {
    loading.value = false
  }
}

const handleStatusChange = async (user: any) => {
  try {
    await request.put(`/users/${user.id}/status`, {
      status: user.status === 1 ? 0 : 1
    })
    ElMessage.success(`用户已${user.status === 1 ? '禁用' : '启用'}`)
    loadUsers()
  } catch (error) {
    console.error(error)
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>