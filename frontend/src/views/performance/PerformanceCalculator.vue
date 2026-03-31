<!-- <template>
  <div>
    <h3>绩效计算详情</h3>
    <p>基本工资: {{ performance.baseSalary }}</p>
    <p>出勤天数: {{ performance.attendanceDays }}</p>
    <p>项目加分: {{ performance.projectBonus }}</p>
    <p>总分: {{ calculateScore() }}</p>
  </div>
</template>
<script lang="ts" setup>
import { ref } from 'vue';
import { useRoute } from 'vue-router';
import request from '../../api/http';

const route = useRoute();
const performance = ref({});

onMounted(async () => {
  const res = await request.get(`/api/performance/${route.params.id}`);  // 获取绩效数据
  performance.value = res.data;
});

const calculateScore = () => {
  // 绩效计算公式
  return (performance.value.baseSalary / 100) + (performance.value.attendanceDays * 2) + performance.value.projectBonus - performance.value.otherDeductions;
};
</script> -->