<template>
  <div class="checkin-container">
    <el-card class="search-container">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="学号">
          <el-input v-model="searchForm.studentNo" placeholder="请输入学号" clearable />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="searchForm.studentName" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="宿舍楼">
          <el-select v-model="searchForm.buildingId" placeholder="请选择宿舍楼" clearable>
            <el-option
              v-for="item in buildingList"
              :key="item.id"
              :label="item.buildingName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="入住中" :value="1" />
            <el-option label="已退房" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="入住日期">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
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
          <span>入住记录列表</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            办理入住
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" width="100" />
        <el-table-column prop="department" label="院系" width="150" />
        <el-table-column prop="major" label="专业" width="120" />
        <el-table-column prop="className" label="班级" width="100" />
        <el-table-column prop="buildingName" label="宿舍楼" width="100" />
        <el-table-column prop="roomNumber" label="宿舍号" width="100" />
        <el-table-column prop="bedNumber" label="床位号" width="80" />
        <el-table-column prop="checkinDate" label="入住日期" width="120">
          <template #default="scope">
            {{ formatDate(scope.row.checkinDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="checkoutDate" label="退房日期" width="120">
          <template #default="scope">
            {{ scope.row.checkoutDate ? formatDate(scope.row.checkoutDate) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '入住中' : '已退房' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button
              v-if="scope.row.status === 1"
              type="warning"
              link
              size="small"
              @click="handleCheckout(scope.row)"
            >
              办理退房
            </el-button>
            <el-button
              v-else
              type="info"
              link
              size="small"
              disabled
            >
              已退房
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

    <el-dialog title="办理入住" v-model="checkinDialogVisible" width="600px">
      <el-form :model="checkinForm" :rules="checkinRules" ref="checkinFormRef" label-width="100px">
        <el-form-item label="学生" prop="studentId">
          <el-select
            v-model="checkinForm.studentId"
            placeholder="请选择学生"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="item in studentList"
              :key="item.id"
              :label="`${item.studentNo} - ${item.name}`"
              :value="item.id"
              :disabled="item.checkinStatus === 1"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="宿舍楼" prop="buildingId">
          <el-select
            v-model="checkinForm.buildingId"
            placeholder="请选择宿舍楼"
            style="width: 100%"
            @change="handleBuildingChange"
          >
            <el-option
              v-for="item in buildingList"
              :key="item.id"
              :label="item.buildingName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="宿舍" prop="dormitoryId">
          <el-select
            v-model="checkinForm.dormitoryId"
            placeholder="请选择宿舍"
            style="width: 100%"
          >
            <el-option
              v-for="item in availableDormitories"
              :key="item.id"
              :label="`${item.roomNumber} (${item.occupiedBeds}/${item.bedCount}床)`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="床位号" prop="bedNumber">
          <el-input-number v-model="checkinForm.bedNumber" :min="1" :max="8" placeholder="请输入床位号" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="checkinForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="checkinDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCheckinSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog title="办理退房" v-model="checkoutDialogVisible" width="400px">
      <el-form :model="checkoutForm" ref="checkoutFormRef" label-width="100px">
        <el-form-item label="学生姓名">
          <el-input :value="currentRecord?.studentName" disabled />
        </el-form-item>
        <el-form-item label="宿舍号">
          <el-input :value="currentRecord?.roomNumber" disabled />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="checkoutForm.remark" type="textarea" :rows="3" placeholder="请输入退房备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="checkoutDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCheckoutSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCheckinPage, checkin, checkout } from '@/api/checkin'
import { getBuildingList } from '@/api/building'
import { getDormitoryAvailable } from '@/api/dormitory'
import { getStudentPage } from '@/api/student'
import dayjs from 'dayjs'

const loading = ref(false)
const checkinDialogVisible = ref(false)
const checkoutDialogVisible = ref(false)
const checkinFormRef = ref(null)
const tableData = ref([])
const buildingList = ref([])
const studentList = ref([])
const availableDormitories = ref([])
const currentRecord = ref(null)

const searchForm = reactive({
  studentNo: '',
  studentName: '',
  buildingId: null,
  status: null,
  dateRange: []
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const checkinForm = reactive({
  studentId: null,
  buildingId: null,
  dormitoryId: null,
  bedNumber: 1,
  remark: ''
})

const checkoutForm = reactive({
  remark: ''
})

const checkinRules = {
  studentId: [{ required: true, message: '请选择学生', trigger: 'change' }],
  buildingId: [{ required: true, message: '请选择宿舍楼', trigger: 'change' }],
  dormitoryId: [{ required: true, message: '请选择宿舍', trigger: 'change' }],
  bedNumber: [{ required: true, message: '请输入床位号', trigger: 'blur' }]
}

const formatDate = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD') : ''
}

const resetCheckinForm = () => {
  checkinForm.studentId = null
  checkinForm.buildingId = null
  checkinForm.dormitoryId = null
  checkinForm.bedNumber = 1
  checkinForm.remark = ''
  availableDormitories.value = []
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

const fetchStudentList = async () => {
  try {
    const res = await getStudentPage({ current: 1, size: 1000 })
    if (res.code === 200) {
      studentList.value = res.data?.records || []
    }
  } catch (error) {
    console.error('获取学生列表失败:', error)
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      studentNo: searchForm.studentNo,
      studentName: searchForm.studentName,
      buildingId: searchForm.buildingId,
      status: searchForm.status
    }
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.startDate = searchForm.dateRange[0]
      params.endDate = searchForm.dateRange[1]
    }
    const res = await getCheckinPage(params)
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
  searchForm.studentNo = ''
  searchForm.studentName = ''
  searchForm.buildingId = null
  searchForm.status = null
  searchForm.dateRange = []
  handleSearch()
}

const handleBuildingChange = async (buildingId) => {
  if (!buildingId) {
    availableDormitories.value = []
    checkinForm.dormitoryId = null
    return
  }
  try {
    const res = await getDormitoryAvailable(buildingId)
    if (res.code === 200) {
      availableDormitories.value = res.data || []
      checkinForm.dormitoryId = null
    }
  } catch (error) {
    ElMessage.error('获取宿舍列表失败')
  }
}

const handleAdd = () => {
  resetCheckinForm()
  checkinDialogVisible.value = true
}

const handleCheckout = (row) => {
  currentRecord.value = row
  checkoutForm.remark = ''
  checkoutDialogVisible.value = true
}

const handleCheckinSubmit = async () => {
  await checkinFormRef.value.validate()
  try {
    const res = await checkin({
      studentId: checkinForm.studentId,
      dormitoryId: checkinForm.dormitoryId,
      bedNumber: checkinForm.bedNumber,
      remark: checkinForm.remark
    })
    if (res.code === 200) {
      ElMessage.success('入住办理成功')
      checkinDialogVisible.value = false
      fetchData()
      fetchStudentList()
    }
  } catch (error) {
    console.error('入住办理失败:', error)
  }
}

const handleCheckoutSubmit = async () => {
  try {
    await ElMessageBox.confirm('确定要办理退房吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await checkout(currentRecord.value.id, checkoutForm.remark)
    if (res.code === 200) {
      ElMessage.success('退房办理成功')
      checkoutDialogVisible.value = false
      fetchData()
      fetchStudentList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('退房办理失败:', error)
    }
  }
}

onMounted(() => {
  fetchBuildingList()
  fetchStudentList()
  fetchData()
})
</script>

<style lang="scss" scoped>
.checkin-container {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
