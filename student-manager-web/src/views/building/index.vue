<template>
  <div class="building-container">
    <el-card class="search-container">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="名称/编号">
          <el-input v-model="searchForm.buildingName" placeholder="请输入名称或编号" clearable />
        </el-form-item>
        <el-form-item label="性别类型">
          <el-select v-model="searchForm.genderType" placeholder="请选择性别类型" clearable>
            <el-option label="男生楼" :value="1" />
            <el-option label="女生楼" :value="2" />
            <el-option label="混合楼" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
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
          <span>宿舍楼列表</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增宿舍楼
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="buildingName" label="宿舍楼名称" width="150" />
        <el-table-column prop="buildingNumber" label="宿舍楼编号" width="120" />
        <el-table-column prop="floorCount" label="楼层数" width="100" />
        <el-table-column prop="roomCountPerFloor" label="每层房间数" width="120" />
        <el-table-column prop="genderType" label="性别类型" width="100">
          <template #default="scope">
            <el-tag :type="getGenderType(scope.row.genderType)">
              {{ getGenderText(scope.row.genderType) }}
            </el-tag>
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
        <el-form-item label="宿舍楼名称" prop="buildingName">
          <el-input v-model="form.buildingName" placeholder="请输入宿舍楼名称" />
        </el-form-item>
        <el-form-item label="宿舍楼编号" prop="buildingNumber">
          <el-input v-model="form.buildingNumber" placeholder="请输入宿舍楼编号" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="楼层数" prop="floorCount">
              <el-input-number v-model="form.floorCount" :min="1" :max="50" placeholder="请输入楼层数" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="每层房间数" prop="roomCountPerFloor">
              <el-input-number v-model="form.roomCountPerFloor" :min="1" :max="100" placeholder="请输入每层房间数" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="性别类型" prop="genderType">
          <el-radio-group v-model="form.genderType">
            <el-radio :value="1">男生楼</el-radio>
            <el-radio :value="2">女生楼</el-radio>
            <el-radio :value="3">混合楼</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
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
import { getBuildingPage, getBuildingById, saveBuilding, updateBuilding, deleteBuilding, updateBuildingStatus } from '@/api/building'
import dayjs from 'dayjs'

const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref(null)
const tableData = ref([])

const searchForm = reactive({
  buildingName: '',
  genderType: null,
  status: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const form = reactive({
  id: null,
  buildingName: '',
  buildingNumber: '',
  floorCount: 6,
  roomCountPerFloor: 10,
  genderType: 1,
  description: '',
  status: 1
})

const dialogTitle = computed(() => form.id ? '编辑宿舍楼' : '新增宿舍楼')

const rules = {
  buildingName: [{ required: true, message: '请输入宿舍楼名称', trigger: 'blur' }],
  buildingNumber: [{ required: true, message: '请输入宿舍楼编号', trigger: 'blur' }],
  floorCount: [{ required: true, message: '请输入楼层数', trigger: 'blur' }],
  roomCountPerFloor: [{ required: true, message: '请输入每层房间数', trigger: 'blur' }],
  genderType: [{ required: true, message: '请选择性别类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const getGenderType = (type) => {
  switch (type) {
    case 1: return 'primary'
    case 2: return 'danger'
    case 3: return 'warning'
    default: return 'info'
  }
}

const getGenderText = (type) => {
  switch (type) {
    case 1: return '男生楼'
    case 2: return '女生楼'
    case 3: return '混合楼'
    default: return '未知'
  }
}

const formatDate = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm:ss') : ''
}

const resetForm = () => {
  form.id = null
  form.buildingName = ''
  form.buildingNumber = ''
  form.floorCount = 6
  form.roomCountPerFloor = 10
  form.genderType = 1
  form.description = ''
  form.status = 1
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getBuildingPage({
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
  searchForm.buildingName = ''
  searchForm.genderType = null
  searchForm.status = null
  handleSearch()
}

const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  resetForm()
  try {
    const res = await getBuildingById(row.id)
    if (res.code === 200) {
      Object.assign(form, res.data)
      dialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取宿舍楼信息失败')
  }
}

const handleStatusChange = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要${row.status === 1 ? '启用' : '禁用'}该宿舍楼吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await updateBuildingStatus(row.id, row.status)
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
    await ElMessageBox.confirm('确定要删除该宿舍楼吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await deleteBuilding(row.id)
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
      res = await updateBuilding(form)
    } else {
      res = await saveBuilding(form)
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
  fetchData()
})
</script>

<style lang="scss" scoped>
.building-container {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
