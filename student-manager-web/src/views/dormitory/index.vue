<template>
  <div class="dormitory-container">
    <el-card class="search-container">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="宿舍楼">
          <el-select v-model="searchForm.buildingId" placeholder="请选择宿舍楼" clearable @change="handleBuildingChange">
            <el-option
              v-for="item in buildingList"
              :key="item.id"
              :label="item.buildingName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="房间号">
          <el-input v-model="searchForm.roomNumber" placeholder="请输入房间号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
            <el-option label="维修中" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-container">
      <template #header>
        <div class="card-header">
          <span>宿舍列表</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增宿舍
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="buildingName" label="宿舍楼" width="120" />
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="floor" label="楼层" width="80" />
        <el-table-column prop="bedCount" label="床位总数" width="100" />
        <el-table-column prop="occupiedBeds" label="已入住" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.occupiedBeds >= scope.row.bedCount ? 'danger' : 'success'">
              {{ scope.row.occupiedBeds }} / {{ scope.row.bedCount }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="roomType" label="房间类型" width="100">
          <template #default="scope">
            {{ getRoomTypeText(scope.row.roomType) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button type="primary" link size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </el-card>

    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="宿舍楼" prop="buildingId">
          <el-select
            v-model="form.buildingId"
            placeholder="请选择宿舍楼"
            style="width: 100%"
            @change="handleFormBuildingChange"
          >
            <el-option
              v-for="item in buildingList"
              :key="item.id"
              :label="item.buildingName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="房间号" prop="roomNumber">
              <el-input v-model="form.roomNumber" placeholder="请输入房间号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="楼层" prop="floor">
              <el-input-number v-model="form.floor" :min="1" :max="50" placeholder="请输入楼层" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="床位数量" prop="bedCount">
              <el-input-number v-model="form.bedCount" :min="1" :max="20" placeholder="请输入床位数量" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房间类型" prop="roomType">
              <el-select v-model="form.roomType" placeholder="请选择房间类型" style="width: 100%">
                <el-option label="标准间" :value="1" />
                <el-option label="豪华间" :value="2" />
                <el-option label="其他" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
            <el-radio :value="2">维修中</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDormitoryPage, getDormitoryById, saveDormitory, updateDormitory, deleteDormitory, updateDormitoryStatus } from '@/api/dormitory'
import { getBuildingList } from '@/api/building'
import dayjs from 'dayjs'

const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref(null)
const tableData = ref([])
const buildingList = ref([])

const searchForm = reactive({
  buildingId: null,
  roomNumber: '',
  status: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const form = reactive({
  id: null,
  buildingId: null,
  roomNumber: '',
  floor: 1,
  bedCount: 4,
  occupiedBeds: 0,
  roomType: 1,
  description: '',
  status: 1
})

const dialogTitle = computed(() => form.id ? '编辑宿舍' : '新增宿舍')

const rules = {
  buildingId: [{ required: true, message: '请选择宿舍楼', trigger: 'change' }],
  roomNumber: [{ required: true, message: '请输入房间号', trigger: 'blur' }],
  floor: [{ required: true, message: '请输入楼层', trigger: 'blur' }],
  bedCount: [{ required: true, message: '请输入床位数量', trigger: 'blur' }],
  roomType: [{ required: true, message: '请选择房间类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const getRoomTypeText = (type) => {
  switch (type) {
    case 1: return '标准间'
    case 2: return '豪华间'
    case 3: return '其他'
    default: return '未知'
  }
}

const getStatusType = (status) => {
  switch (status) {
    case 1: return 'success'
    case 0: return 'danger'
    case 2: return 'warning'
    default: return 'info'
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 1: return '启用'
    case 0: return '禁用'
    case 2: return '维修中'
    default: return '未知'
  }
}

const formatDate = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm:ss') : ''
}

const resetForm = () => {
  form.id = null
  form.buildingId = null
  form.roomNumber = ''
  form.floor = 1
  form.bedCount = 4
  form.occupiedBeds = 0
  form.roomType = 1
  form.description = ''
  form.status = 1
}

const fetchBuildingList = async () => {
  try {
    const res = await getBuildingList()
    if (res.code === 200) {
      buildingList.value = res.data || []
    }
  } catch (error) {
    console.error('获取宿舍楼列表失败:', error)
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getDormitoryPage({
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    })
    if (res.code === 200) {
      tableData.value = res.data?.records || []
      pagination.total = res.data?.total || 0
    }
  } catch (error) {
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

const handleReset = () => {
  searchForm.buildingId = null
  searchForm.roomNumber = ''
  searchForm.status = null
  handleSearch()
}

const handleBuildingChange = () => {
  handleSearch()
}

const handleFormBuildingChange = () => {
  form.roomNumber = ''
  form.floor = 1
}

const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  resetForm()
  try {
    const res = await getDormitoryById(row.id)
    if (res.code === 200) {
      Object.assign(form, res.data)
      dialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取宿舍信息失败')
  }
}

const handleStatusChange = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要${row.status === 1 ? '启用' : '禁用'}该宿舍吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await updateDormitoryStatus(row.id, row.status)
    if (res.code === 200) {
      ElMessage.success('状态更新成功')
    }
  } catch (error) {
    if (error !== 'cancel') {
      row.status = row.status === 1 ? 0 : 1
      ElMessage.error('状态更新失败')
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该宿舍吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await deleteDormitory(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchData()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  await formRef.value.validate()
  try {
    let res
    if (form.id) {
      res = await updateDormitory(form)
    } else {
      res = await saveDormitory(form)
    }
    if (res.code === 200) {
      ElMessage.success(form.id ? '更新成功' : '添加成功')
      dialogVisible.value = false
      fetchData()
    }
  } catch (error) {
    console.error('操作失败:', error)
  }
}

onMounted(() => {
  fetchBuildingList()
  fetchData()
})
</script>

<style lang="scss" scoped>
.dormitory-container {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
