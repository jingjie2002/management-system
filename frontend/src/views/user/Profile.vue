<template>
  <div class="profile-container">
    <el-skeleton v-if="loading" :rows="8" animated />

    <template v-else-if="profile">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header card-header-between">
            <span>个人信息看板</span>
            <el-button
              v-if="roleKeys.includes('employee')"
              type="primary"
              plain
              @click="goToMyChange"
            >
              查看我的异动
            </el-button>
          </div>
        </template>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="员工ID">{{ profile.employeeId ?? '未关联' }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ profile.name || '-' }}</el-descriptions-item>
          <el-descriptions-item label="电话">{{ profile.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="工号">{{ profile.jobNumber || '-' }}</el-descriptions-item>
          <el-descriptions-item label="部门">{{ profile.deptName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="岗位">{{ profile.position || '-' }}</el-descriptions-item>
          <el-descriptions-item label="当前职级">{{ profile.jobLevelName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="入职日期">{{ profile.hireDate || '-' }}</el-descriptions-item>
          <el-descriptions-item label="离职日期">{{ profile.leaveDate || '在职' }}</el-descriptions-item>
        </el-descriptions>

        <div class="score-box">
          <div class="score-label">项目参与贡献总分</div>
          <div class="score-value">{{ profile.projectContributionScore ?? 0 }}</div>
        </div>
      </el-card>

      <el-card class="box-card" shadow="hover">
        <template #header><span>近期异动记录</span></template>
        <el-table :data="profile.recentChanges || []" size="small" stripe empty-text="暂无异动记录">
          <el-table-column label="异动类型" width="120">
            <template #default="{ row }">
              {{ changeTypeText(row.changeType) }}
            </template>
          </el-table-column>
          <el-table-column label="状态" width="150">
            <template #default="{ row }">
              <el-tag :type="changeStatusTag(row.status)">
                {{ changeStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="effectiveDate" label="生效日期" width="120" />
          <el-table-column label="目标部门" min-width="150">
            <template #default="{ row }">
              {{ departmentLabel(departmentOptions, row.afterDeptId) }}
            </template>
          </el-table-column>
          <el-table-column prop="afterPosition" label="目标岗位" min-width="140" />
          <el-table-column label="目标职级" min-width="160">
            <template #default="{ row }">
              {{ jobLevelLabel(jobLevels, row.afterJobLevelId) }}
            </template>
          </el-table-column>
          <el-table-column label="调整薪资" width="120">
            <template #default="{ row }">
              {{ formatMoney(row.afterSalary) }}
            </template>
          </el-table-column>
          <el-table-column prop="reason" label="异动原因" min-width="200" show-overflow-tooltip />
        </el-table>
      </el-card>

      <el-card class="box-card" shadow="hover">
        <template #header><span>考勤统计</span></template>
        <div class="summary-box">
          <div
            v-for="item in attendanceItems"
            :key="item.key"
            class="summary-item"
          >
            <div class="summary-label">{{ item.key }}</div>
            <div class="summary-value">{{ item.value }} 天</div>
          </div>
        </div>
      </el-card>

      <el-card class="box-card" shadow="hover">
        <template #header><span>近期绩效（近 6 条）</span></template>
        <el-table :data="profile.performanceHistory || []" size="small" stripe>
          <el-table-column prop="perfMonth" label="月份" width="140" />
          <el-table-column prop="baseSalary" label="基本工资" width="120" />
          <el-table-column prop="attendanceDays" label="出勤天数" width="120" />
          <el-table-column prop="comments" label="评语" />
        </el-table>
      </el-card>

      <el-card class="box-card" shadow="hover">
        <template #header><span>请假记录（近 10 条）</span></template>
        <el-table :data="profile.leaveRecords || []" size="small" stripe>
          <el-table-column prop="type" label="类型" width="120" />
          <el-table-column prop="startTime" label="开始时间" width="180" />
          <el-table-column prop="endTime" label="结束时间" width="180" />
          <el-table-column prop="status" label="状态" width="120">
            <template #default="scope">
              <el-tag :type="leaveTagType(scope.row.status)">
                {{ leaveStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="reason" label="原因" />
        </el-table>
      </el-card>

      <el-card class="box-card" shadow="hover">
        <template #header><span>项目参与贡献明细</span></template>
        <el-table :data="profile.projectContributions || []" size="small" stripe>
          <el-table-column prop="projectName" label="项目" />
          <el-table-column prop="role" label="角色" width="140" />
          <el-table-column prop="projectStatus" label="项目状态" width="120">
            <template #default="scope">
              {{ projectStatusText(scope.row.projectStatus) }}
            </template>
          </el-table-column>
          <el-table-column prop="startDate" label="开始日期" width="130" />
          <el-table-column prop="endDate" label="结束日期" width="130" />
          <el-table-column prop="contributionScore" label="贡献分" width="120" />
        </el-table>
      </el-card>

      <el-card class="box-card" shadow="hover">
        <template #header><span>最新公告</span></template>
        <el-table :data="notices" size="small" stripe>
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="type" label="类型" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.type === 1 ? 'primary' : 'warning'">
                {{ scope.row.type === 1 ? '通知' : '公告' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="发布时间" width="180" />
          <el-table-column label="操作" width="100">
            <template #default="scope">
              <el-button size="small" @click="handleNoticeDetail(scope.row)">查看</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </template>

    <el-empty v-else description="未获取到个人档案数据" />

    <el-dialog
      v-model="noticeDialogVisible"
      :title="noticeDialogTitle"
      width="600px"
    >
      <div class="notice-detail">
        <div class="notice-meta">
          <span class="notice-type">{{ currentNotice.type === 1 ? '通知' : '公告' }}</span>
          <span class="notice-time">{{ currentNotice.createTime }}</span>
        </div>
        <div class="notice-content">{{ currentNotice.content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import request from '../../api/http'
import { readRoleKeys } from '../../utils/access'
import {
  changeStatusTag,
  changeStatusText,
  changeTypeText,
  departmentLabel,
  flattenDepartments,
  formatMoney,
  jobLevelLabel,
  type DepartmentNode,
  type DepartmentOption,
  type EmployeeChangeRecord,
  type JobLevelOption
} from '../../utils/employee-change'

interface AttendanceSummary {
  [key: string]: number
}

interface ProfileData {
  employeeId: number | null
  name: string
  phone: string
  jobNumber: string
  deptName: string
  position: string
  jobLevelName: string
  hireDate: string
  leaveDate: string | null
  projectContributionScore: number
  attendanceSummary: AttendanceSummary
  performanceHistory: any[]
  leaveRecords: any[]
  projectContributions: any[]
  recentChanges: EmployeeChangeRecord[]
}

const router = useRouter()
const loading = ref(true)
const profile = ref<ProfileData | null>(null)
const notices = ref<any[]>([])
const noticeDialogVisible = ref(false)
const noticeDialogTitle = ref('')
const currentNotice = ref<any>({})
const roleKeys = readRoleKeys()
const departmentOptions = ref<DepartmentOption[]>([])
const jobLevels = ref<JobLevelOption[]>([])

const attendanceItems = computed(() => {
  const summary = profile.value?.attendanceSummary || {}
  const keys = ['正常', '迟到', '早退', '缺勤']
  return keys.map(key => ({
    key,
    value: summary[key] ?? 0
  }))
})

const leaveStatusText = (status: number) => {
  if (status === 1) return '已通过'
  if (status === 2) return '已驳回'
  return '待审批'
}

const leaveTagType = (status: number) => {
  if (status === 1) return 'success'
  if (status === 2) return 'danger'
  return 'warning'
}

const projectStatusText = (status: number) => {
  if (status === 1) return '进行中'
  if (status === 2) return '已完成'
  if (status === 3) return '已暂停'
  return '待处理'
}

const fetchProfile = async () => {
  loading.value = true
  try {
    const [profileRes, departmentRes, jobLevelRes] = await Promise.all([
      request.get('/employees/profile'),
      request.get('/departments/tree'),
      request.get('/job-levels')
    ])

    profile.value = profileRes.data as ProfileData
    departmentOptions.value = flattenDepartments((departmentRes.data || []) as DepartmentNode[])
    jobLevels.value = (jobLevelRes.data || []) as JobLevelOption[]
  } finally {
    loading.value = false
  }
}

const fetchNotices = async () => {
  try {
    const res = await request.get('/notices', {
      params: {
        page: 1,
        size: 5
      }
    })
    notices.value = res.data.records
  } catch (error) {
    console.error(error)
  }
}

const handleNoticeDetail = (notice: any) => {
  currentNotice.value = notice
  noticeDialogTitle.value = notice.title
  noticeDialogVisible.value = true
}

const goToMyChange = () => {
  router.push('/my-change')
}

onMounted(() => {
  fetchProfile()
  fetchNotices()
})
</script>

<style scoped>
.profile-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.card-header {
  font-weight: 600;
}

.card-header-between {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.score-box {
  margin-top: 16px;
  padding: 12px;
  border-radius: 8px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.score-label {
  color: #606266;
}

.score-value {
  font-size: 24px;
  font-weight: 700;
  color: #409eff;
}

.summary-box {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.summary-item {
  min-width: 120px;
  padding: 10px 14px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
}

.summary-label {
  color: #909399;
  font-size: 13px;
}

.summary-value {
  margin-top: 4px;
  font-size: 20px;
  font-weight: 700;
}

.box-card {
  margin-top: 0;
}

.notice-detail {
  padding: 10px 0;
}

.notice-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.notice-type {
  font-weight: bold;
  color: #409eff;
}

.notice-time {
  color: #909399;
  font-size: 12px;
}

.notice-content {
  line-height: 1.6;
  color: #303133;
  white-space: pre-wrap;
}
</style>
