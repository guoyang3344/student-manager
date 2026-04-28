<template>
  <div class="repair-container">
    <el-card class="search-container">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="报修类型">
          <el-select v-model="searchForm.repairType" placeholder="请选择报修类型" clearable>
            <el-option label="水电维修" :value="1" />
            <el-option label="家具维修" :value="2" />
            <el-option label="网络问题" :value="3" />
            <el-option label="其他" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待处理" :value="1" />
            <el-option label="处理中" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已关闭" :value="4" />
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
          <el-button type="success" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            提交报修
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-container">
      <template #header>
        <div class="card-header">
          <span>报修列表</span>
        </div>
      </template>

      <el-table :data="tableData" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="studentName" label="报修人" width="100" />
        <el-table-column prop="buildingName" label="宿舍楼" width="120" />
        <el-table-column prop="roomNumber" label="宿舍号" width="100" />
        <el-table-column prop="repairType" label="报修类型" width="100">
          <template #default="scope">
            {{ getRepairTypeText(scope.row.repairType) }}
          </template>
        </el-table-column>
        <el-table-column prop="title" label="报修标题" width="150" show-overflow-tooltip />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="handlerName" label="处理人" width="100">
          <template #default="scope">
            {{ scope.row.handlerName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="报修时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" link size="small" @click="handleView(scope.row)">
              查看
            </el-button>
            <el-button
              v-if="scope.row.status === 1 || scope.row.status === 2"
              type="success"
              link
              size="small"
              @click="handleProcess(scope.row)"
            >
              处理
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

    <el-dialog title="提交报修" v-model="addDialogVisible" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="报修类型" prop="repairType">
          <el-select v-model="form.repairType" placeholder="请选择报修类型" style="width: 100%">
            <el-option label="水电维修" :value="1" />
            <el-option label="家具维修" :value="2" />
            <el-option label="网络问题" :value="3" />
            <el-option label="其他" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="报修标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入报修标题" maxlength="50" />
        </el-form-item>
        <el-form-item label="报修描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请详细描述报修问题"
            maxlength="500"
          />
        </el-form-item>
        <el-form-item label="报修图片">
          <el-upload
            action="#"
            list-type="picture-card"
            :auto-upload="false"
            :limit="5"
            accept="image/*"
          >
            <el-icon><Plus /></el-icon>
            <template #tip>
              <div class="el-upload__tip">只能上传 jpg/png 文件，且不超过 2MB</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
      </template>
    </el-dialog>

    <el-dialog title="报修详情" v-model="viewDialogVisible" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="报修人">{{ viewData.studentName }}</el-descriptions-item>
        <el-descriptions-item label="学号">{{ viewData.studentNo }}</el-descriptions-item>
        <el-descriptions-item label="宿舍楼">{{ viewData.buildingName }}</el-descriptions-item>
        <el-descriptions-item label="宿舍号">{{ viewData.roomNumber }}</el-descriptions-item>
        <el-descriptions-item label="报修类型">{{ getRepairTypeText(viewData.repairType) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(viewData.status)">
            {{ getStatusText(viewData.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="报修标题" :span="2">{{ viewData.title }}</el-descriptions-item>
        <el-descriptions-item label="报修描述" :span="2">{{ viewData.description }}</el-descriptions-item>
        <el-descriptions-item label="报修时间" :span="2">{{ formatDate(viewData.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="处理人" v-if="viewData.handlerName">{{ viewData.handlerName }}</el-descriptions-item>
        <el-descriptions-item label="处理时间" v-if="viewData.handleTime">{{ formatDate(viewData.handleTime) }}</el-descriptions-item>
        <el-descriptions-item label="处理备注" :span="2" v-if="viewData.handleRemark">
          {{ viewData.handleRemark }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog title="处理报修" v-model="processDialogVisible" width="500px">
      <el-descriptions :column="2" border style="margin-bottom: 20px">
        <el-descriptions-item label="报修人">{{ processData.studentName }}</el-descriptions-item>
        <el-descriptions-item label="宿舍">{{ processData.buildingName }} - {{ processData.roomNumber }}</el-descriptions-item>
        <el-descriptions-item label="报修标题" :span="2">{{ processData.title }}</el-descriptions-item>
        <el-descriptions-item label="报修描述" :span="2">{{ processData.description }}</el-descriptions-item>
      </el-descriptions>
      <el-form :model="processForm" label-width="100px">
        <el-form-item label="处理状态">
          <el-radio-group v-model="processForm.status">
            <el-radio :value="2">处理中</el-radio>
            <el-radio :value="3">已完成</el-radio>
            <el-radio :value="4">已关闭</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理备注">
          <el-input
            v-model="processForm.handleRemark"
            type="textarea"
            :rows="4"
            placeholder="请输入处理备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="processDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleProcessSubmit">确认处理</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getRepairPage, createRepair, updateRepairStatus } from '@/api/repair'
import dayjs from 'dayjs'

const loading = ref(false)
const addDialogVisible = ref(false)
const viewDialogVisible = ref(false)
const processDialogVisible = ref(false)
const formRef = ref(null)
const tableData = ref([])

const searchForm = reactive({
  repairType: null,
  status: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const form = reactive({
  studentId: null,
  repairType: null,
  title: '',
  description: ''
})

const viewData = ref({})
const processData = ref({})
const processForm = reactive({
  status: 2,
  handleRemark: ''
})

const rules = {
  repairType: [{ required: true, message: '请选择报修类型', trigger: 'change' }],
  title: [{ required: true, message: '请输入报修标题', trigger: 'blur' }],
  description: [{ required: true, message: '请输入报修描述', trigger: 'blur' }]
}

const getRepairTypeText = (type) => {
  const map = {
    1: '水电维修',
    2: '家具维修',
    3: '网络问题',
    4: '其他'
  }
  return map[type] || '未知'
}

const getStatusText = (status) => {
  const map = {
    1: '待处理',
    2: '处理中',
    3: '已完成',
    4: '已关闭'
  }
  return map[status] || '未知'
}

const getStatusType = (status) => {
  const map = {
    1: 'warning',
    2: 'primary',
    3: 'success',
    4: 'info'
  }
  return map[status] || 'info'
}

const formatDate = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm:ss') : ''
}

const resetForm = () => {
  form.studentId = null
  form.repairType = null
  form.title = ''
  form.description = ''
}

const resetProcessForm = () => {
  processForm.status = 2
  processForm.handleRemark = ''
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getRepairPage({
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
  searchForm.repairType = null
  searchForm.status = null
  handleSearch()
}

const handleAdd = () => {
  resetForm()
  addDialogVisible.value = true
}

const handleView = (row) => {
  viewData.value = row
  viewDialogVisible.value = true
}

const handleProcess = (row) => {
  processData.value = row
  resetProcessForm()
  processDialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  
  try {
    const res = await createRepair(form)
    if (res.code === 200) {
      ElMessage.success('报修提交成功')
      addDialogVisible.value = false
      fetchData()
    }
  } catch (error) {
    console.error('提交失败:', error)
  }
}

const handleProcessSubmit = async () => {
  try {
    const res = await updateRepairStatus(processData.value.id, {
      status: processForm.status,
      handleRemark: processForm.handleRemark
    })
    if (res.code === 200) {
      ElMessage.success('处理成功')
      processDialogVisible.value = false
      fetchData()
    }
  } catch (error) {
    console.error('处理失败:', error)
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style lang="scss" scoped>
.repair-container {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
