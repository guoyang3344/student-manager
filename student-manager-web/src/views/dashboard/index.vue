<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :xs="12" :sm="12" :lg="6">
        <div class="card-box">
          <div class="card-icon user-icon">
            <el-icon :size="32"><User /></el-icon>
          </div>
          <div class="card-info">
            <p class="card-value">{{ stats.userCount }}</p>
            <p class="card-label">用户总数</p>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6">
        <div class="card-box">
          <div class="card-icon student-icon">
            <el-icon :size="32"><Reading /></el-icon>
          </div>
          <div class="card-info">
            <p class="card-value">{{ stats.studentCount }}</p>
            <p class="card-label">学生总数</p>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6">
        <div class="card-box">
          <div class="card-icon building-icon">
            <el-icon :size="32"><OfficeBuilding /></el-icon>
          </div>
          <div class="card-info">
            <p class="card-value">{{ stats.buildingCount }}</p>
            <p class="card-label">宿舍楼总数</p>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6">
        <div class="card-box">
          <div class="card-icon checkin-icon">
            <el-icon :size="32"><Document /></el-icon>
          </div>
          <div class="card-info">
            <p class="card-value">{{ stats.checkinCount }}</p>
            <p class="card-label">当前入住人数</p>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>最近入住记录</span>
            </div>
          </template>
          <el-table :data="recentRecords" stripe style="width: 100%">
            <el-table-column prop="studentName" label="学生姓名" />
            <el-table-column prop="studentNo" label="学号" />
            <el-table-column prop="buildingName" label="宿舍楼" />
            <el-table-column prop="roomNumber" label="宿舍号" />
            <el-table-column prop="checkinDate" label="入住日期" />
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
                  {{ scope.row.status === 1 ? '入住中' : '已退房' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>系统使用说明</span>
            </div>
          </template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="用户管理">
              管理系统用户，支持添加、修改、删除、重置密码等操作
            </el-descriptions-item>
            <el-descriptions-item label="角色管理">
              管理系统角色，支持添加、修改、删除角色
            </el-descriptions-item>
            <el-descriptions-item label="学生档案">
              管理学生信息，支持添加、修改、删除学生档案
            </el-descriptions-item>
            <el-descriptions-item label="宿舍楼管理">
              管理宿舍楼信息，支持添加、修改、删除宿舍楼
            </el-descriptions-item>
            <el-descriptions-item label="宿舍管理">
              管理宿舍信息，支持添加、修改、删除宿舍
            </el-descriptions-item>
            <el-descriptions-item label="入住记录查询">
              查看学生入住/退房记录，支持办理入住和退房
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStudentPage } from '@/api/student'
import { getBuildingList } from '@/api/building'
import { getUserPage } from '@/api/user'
import { getCheckinPage } from '@/api/checkin'
import { ElMessage } from 'element-plus'

const stats = ref({
  userCount: 0,
  studentCount: 0,
  buildingCount: 0,
  checkinCount: 0
})

const recentRecords = ref([])

const fetchStats = async () => {
  try {
    const [userRes, studentRes, buildingRes, checkinRes] = await Promise.all([
      getUserPage({ current: 1, size: 1 }),
      getStudentPage({ current: 1, size: 1 }),
      getBuildingList(),
      getCheckinPage({ current: 1, size: 10, status: 1 })
    ])
    
    stats.value.userCount = userRes.data?.total || 0
    stats.value.studentCount = studentRes.data?.total || 0
    stats.value.buildingCount = buildingRes.data?.length || 0
    stats.value.checkinCount = checkinRes.data?.total || 0
    
    if (checkinRes.data?.records) {
      recentRecords.value = checkinRes.data.records.slice(0, 5)
    }
  } catch (error) {
    ElMessage.error('获取统计数据失败')
  }
}

onMounted(() => {
  fetchStats()
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  .card-box {
    display: flex;
    align-items: center;
    padding: 20px;
    background: #fff;
    border-radius: 4px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    
    .card-icon {
      width: 60px;
      height: 60px;
      border-radius: 4px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      
      &.user-icon {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      }
      &.student-icon {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      }
      &.building-icon {
        background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      }
      &.checkin-icon {
        background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
      }
    }
    
    .card-info {
      margin-left: 16px;
      
      .card-value {
        font-size: 28px;
        font-weight: bold;
        color: #333;
        margin: 0;
      }
      
      .card-label {
        font-size: 14px;
        color: #999;
        margin: 4px 0 0 0;
      }
    }
  }
  
  .box-card {
    .card-header {
      font-weight: bold;
      color: #333;
    }
  }
}
</style>
