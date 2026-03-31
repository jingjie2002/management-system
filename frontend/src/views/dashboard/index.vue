<template>
  <div class="dashboard-container">
    <div class="toolbar">
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        value-format="YYYY-MM-DD"
        range-separator="至"
        start-placeholder="统计开始日期"
        end-placeholder="统计结束日期"
      />
      <el-button type="primary" @click="fetchStats">刷新统计</el-button>
    </div>

    <div class="stats-cards">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>员工总数</span>
              </div>
            </template>
            <div class="card-value">{{ stats.totalEmployees }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>在职员工</span>
              </div>
            </template>
            <div class="card-value text-success">{{ stats.activeEmployees }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>部门总数</span>
              </div>
            </template>
            <div class="card-value">{{ stats.totalDepartments }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>进行中项目</span>
              </div>
            </template>
            <div class="card-value text-primary">{{ stats.ongoingProjects }}</div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="charts-row">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card shadow="hover">
            <template #header>
              <span>部门人员分布</span>
            </template>
            <div ref="deptChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover">
            <template #header>
              <span>考勤状态分布</span>
            </template>
            <div ref="attChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover">
            <template #header>
              <span>项目状态分布</span>
            </template>
            <div ref="projChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="charts-row">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <span>考勤率 Top5 部门（最小堆）</span>
            </template>
            <el-table :data="stats.topAttendanceDepartments" size="small" border>
              <el-table-column prop="name" label="部门" />
              <el-table-column prop="value" label="考勤率(%)" />
            </el-table>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <span>员工贡献 Top5（加权分）</span>
            </template>
            <el-table :data="stats.employeeContributionTop" size="small" border>
              <el-table-column prop="name" label="员工" />
              <el-table-column prop="value" label="贡献分" />
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="charts-row">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <span>请假审批分布</span>
            </template>
            <div ref="leaveChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <span>近6个月绩效趋势</span>
            </template>
            <div ref="perfTrendChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, nextTick } from 'vue'
import request from '../../api/http'
import * as echarts from 'echarts'

const stats = ref({
  totalEmployees: 0,
  activeEmployees: 0,
  resignedEmployees: 0,
  totalDepartments: 0,
  totalProjects: 0,
  ongoingProjects: 0,
  deptEmployeeStats: [],
  attendanceStats: [],
  projectStatusStats: [],
  topAttendanceDepartments: [],
  employeeContributionTop: [],
  leaveStatusStats: [],
  monthlyPerformanceTrend: []
})

const deptChartRef = ref(null)
const attChartRef = ref(null)
const projChartRef = ref(null)
const leaveChartRef = ref(null)
const perfTrendChartRef = ref(null)
const dateRange = ref<string[]>([])

// 拉取仪表盘汇总数据
const fetchStats = async () => {
  try {
    const params: any = {}
    if (dateRange.value && dateRange.value.length === 2) {
      params.fromDate = dateRange.value[0]
      params.toDate = dateRange.value[1]
    }
    const res = await request.get('/statistics/dashboard', { params })
    stats.value = res.data
    initCharts()
  } catch (error) {
    console.error(error)
  }
}

const initCharts = () => {
  nextTick(() => {
    // 部门人数占比图
    if (deptChartRef.value) {
      const chart = echarts.init(deptChartRef.value)
      chart.setOption({
        tooltip: { trigger: 'item' },
        series: [
          {
            type: 'pie',
            radius: ['40%', '70%'],
            data: stats.value.deptEmployeeStats
          }
        ]
      })
    }
    
    // 今日考勤占比图
    if (attChartRef.value) {
      const chart = echarts.init(attChartRef.value)
      chart.setOption({
        tooltip: { trigger: 'item' },
        series: [
          {
            type: 'pie',
            radius: '50%',
            data: stats.value.attendanceStats
          }
        ]
      })
    }

    // 项目状态柱状图
    if (projChartRef.value) {
      const chart = echarts.init(projChartRef.value)
      chart.setOption({
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: stats.value.projectStatusStats.map((i: any) => i.name) },
        yAxis: { type: 'value' },
        series: [
          {
            type: 'bar',
            data: stats.value.projectStatusStats.map((i: any) => i.value),
            itemStyle: { color: '#409EFF' }
          }
        ]
      })
    }

    if (leaveChartRef.value) {
      const chart = echarts.init(leaveChartRef.value)
      chart.setOption({
        tooltip: { trigger: 'item' },
        series: [
          {
            type: 'pie',
            radius: ['35%', '68%'],
            data: stats.value.leaveStatusStats
          }
        ]
      })
    }

    if (perfTrendChartRef.value) {
      const chart = echarts.init(perfTrendChartRef.value)
      chart.setOption({
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: stats.value.monthlyPerformanceTrend.map((i: any) => i.name) },
        yAxis: { type: 'value' },
        series: [
          {
            type: 'line',
            smooth: true,
            data: stats.value.monthlyPerformanceTrend.map((i: any) => i.value)
          }
        ]
      })
    }
  })
}

onMounted(() => {
  fetchStats()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}
.stats-cards {
  margin-bottom: 20px;
}
.toolbar {
  margin-bottom: 16px;
  display: flex;
  gap: 10px;
  align-items: center;
}
.card-header {
  font-weight: bold;
}
.card-value {
  font-size: 24px;
  font-weight: bold;
  text-align: center;
  padding: 10px 0;
}
.text-success {
  color: #67C23A;
}
.text-primary {
  color: #409EFF;
}
.charts-row {
  margin-top: 20px;
}
.chart-container {
  height: 300px;
}
</style>
