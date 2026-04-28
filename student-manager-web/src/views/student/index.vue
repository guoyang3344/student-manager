<template>
  <div class="student-container">
    <el-card class="search-container">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="学号">
          <el-input v-model="searchForm.studentNo" placeholder="请输入学号" clearable />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="院系">
          <el-input v-model="searchForm.department" placeholder="请输入院系" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="在读" :value="1" />
            <el-option label="禁用" :value="0" />
            <el-option label="已毕业" :value="2" />
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
          <span>学生列表</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增学生
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="scope">
            {{ scope.row.gender === 1 ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column prop="department" label="院系" width="150" />
        <el-table-column prop="major" label="专业" width="120" />
        <el-table-column prop="className" label="班级" width="100" />
        <el-table-column prop="grade" label="年级" width="80" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="buildingName" label="宿舍楼" width="100">
          <template #default="scope">
            {{ scope.row.buildingName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="dormitoryRoomNumber" label="宿舍号" width="100">
          <template #default="scope">
            {{ scope.row.dormitoryRoomNumber || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="checkinStatus" label="入住状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.checkinStatus === 1 ? 'success' : 'info'">
              {{ scope.row.checkinStatus === 1 ? '已入住' : '未入住' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button type="primary" link size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button 
              v-if="scope.row.checkinStatus !== 1" 
              type="success" 
              link 
              size="small" 
              @click="handleCheckin(scope.row)"
            >
              办理入住
            </el-button>
            <el-button 
              v-if="scope.row.checkinStatus === 1" 
              type="warning" 
              link 
              size="small" 
              @click="handleCheckout(scope.row)"
            >
              办理退房
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
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学号" prop="studentNo">
              <el-input v-model="form.studentNo" placeholder="请输入学号" :disabled="form.id" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio :value="1">男</el-radio>
                <el-radio :value="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthday">
              <el-date-picker
                v-model="form.birthday"
                type="date"
                placeholder="请选择出生日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="form.idCard" placeholder="请输入身份证号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="院系" prop="department">
              <el-input v-model="form.department" placeholder="请输入院系" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="专业" prop="major">
              <el-input v-model="form.major" placeholder="请输入专业" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="班级" prop="className">
              <el-input v-model="form.className" placeholder="请输入班级" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年级" prop="grade">
              <el-input-number v-model="form.grade" :min="2000" :max="2030" placeholder="请输入年级" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="家庭地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入家庭地址" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">在读</el-radio>
            <el-radio :value="0">禁用</el-radio>
            <el-radio :value="2">已毕业</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog title="办理入住" v-model="checkinDialogVisible" width="500px">
      <el-form :model="checkinForm" :rules="checkinRules" ref="checkinFormRef" label-width="100px">
        <el-form-item label="学生姓名">
          <el-input :value="currentStudent?.name" disabled />
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
      <el-form :model="checkoutForm" ref="checkoutFormRef" label-width="80px">
        <el-form-item label="学生姓名">
          <el-input :value="currentStudent?.name" disabled />
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getStudentPage, getStudentById, saveStudent, updateStudent, deleteStudent } from '@/api/student'
import { getBuildingList } from '@/api/building'
import { getDormitoryAvailable } from '@/api/dormitory'
import { checkin, checkout, getActiveCheckinByStudent } from '@/api/checkin'
import dayjs from 'dayjs'

const loading = ref(false)
const dialogVisible = ref(false)
const checkinDialogVisible = ref(false)
const checkoutDialogVisible = ref(false)
const formRef = ref(null)
const checkinFormRef = ref(null)
const tableData = ref([])
const buildingList = ref([])
const availableDormitories = ref([])
const currentStudent = ref(null)
const activeCheckin = ref(null)

const searchForm = reactive({
  studentNo: '',
  name: '',
  department: '',
  status: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const form = reactive({
  id: null,
  studentNo: '',
  name: '',
  gender: 1,
  birthday: '',
  idCard: '',
  phone: '',
  email: '',
  department: '',
  major: '',
  className: '',
  grade: 2024,
  address: '',
  status: 1
})

const checkinForm = reactive({
  buildingId: null,
  dormitoryId: null,
  bedNumber: 1,
  remark: ''
})

const checkoutForm = reactive({
  remark: ''
})

const dialogTitle = computed(() => form.id ? '编辑学生' : '新增学生')

const rules = {
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  department: [{ required: true, message: '请输入院系', trigger: 'blur' }],
  major: [{ required: true, message: '请输入专业', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const checkinRules = {
  buildingId: [{ required: true, message: '请选择宿舍楼', trigger: 'change' }],
  dormitoryId: [{ required: true, message: '请选择宿舍', trigger: 'change' }],
  bedNumber: [{ required: true, message: '请输入床位号', trigger: 'blur' }]
}

const getStatusType = (status) => {
  switch (status) {
    case 1: return 'success'
    case 0: return 'danger'
    case 2: return 'info'
    default: return 'info'
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 1: return '在读'
    case 0: return '禁用'
    case 2: return '已毕业'
    default: return '未知'
  }
}

const resetForm = () => {
  form.id = null
  form.studentNo = ''
  form.name = ''
  form.gender = 1
  form.birthday = ''
  form.idCard = ''
  form.phone = ''
  form.email = ''
  form.department = ''
  form.major = ''
  form.className = ''
  form.grade = 2024
  form.address = ''
  form.status = 1
}

const resetCheckinForm = () => {
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

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getStudentPage({
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
  searchForm.studentNo = ''
  searchForm.name = ''
  searchForm.department = ''
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
    const res = await getStudentById(row.id)
    if (res.code === 200) {
      Object.assign(form, res.data)
      dialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取学生信息失败')
  }
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

const handleCheckin = async (row) => {
  currentStudent.value = row
  resetCheckinForm()
  checkinDialogVisible.value = true
}

const handleCheckout = async (row) => {
  currentStudent.value = row
  try {
    const res = await getActiveCheckinByStudent(row.id)
    if (res.code === 200 && res.data) {
      activeCheckin.value = res.data
      checkoutForm.remark = ''
      checkoutDialogVisible.value = true
    } else {
      ElMessage.error('未找到有效的入住记录')
    }
  } catch (error) {
    ElMessage.error('获取入住记录失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该学生吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await deleteStudent(row.id)
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
      res = await updateStudent(form)
    } else {
      res = await saveStudent(form)
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

const handleCheckinSubmit = async () => {
  await checkinFormRef.value.validate()
  try {
    const res = await checkin({
      studentId: currentStudent.value.id,
      dormitoryId: checkinForm.dormitoryId,
      bedNumber: checkinForm.bedNumber,
      remark: checkinForm.remark
    })
    if (res.code === 200) {
      ElMessage.success('入住办理成功')
      checkinDialogVisible.value = false
      fetchData()
    }
  } catch (error) {
    console.error('入住办理失败:', error)
  }
}

const handleCheckoutSubmit = async () => {
  try {
    const res = await checkout(activeCheckin.value.id, checkoutForm.remark)
    if (res.code === 200) {
      ElMessage.success('退房办理成功')
      checkoutDialogVisible.value = false
      fetchData()
    }
  } catch (error) {
    console.error('退房办理失败:', error)
  }
}

onMounted(() => {
  fetchBuildingList()
  fetchData()
})
</script>

<style lang="scss" scoped>
.student-container {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
