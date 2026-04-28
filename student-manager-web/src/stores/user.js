import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, getInfo, logout } from '@/api/auth'
import { ElMessage } from 'element-plus'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

  const isLoggedIn = computed(() => !!token.value)

  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setUserInfo = (info) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  const clearToken = () => {
    token.value = ''
    localStorage.removeItem('token')
  }

  const clearUserInfo = () => {
    userInfo.value = {}
    localStorage.removeItem('userInfo')
  }

  const handleLogin = async (loginForm) => {
    try {
      const res = await login(loginForm)
      if (res.code === 200) {
        setToken(res.data.token)
        setUserInfo(res.data.user)
        ElMessage.success('登录成功')
        router.push('/')
        return true
      } else {
        ElMessage.error(res.message || '登录失败')
        return false
      }
    } catch (error) {
      ElMessage.error('登录失败，请稍后重试')
      return false
    }
  }

  const fetchUserInfo = async () => {
    try {
      const res = await getInfo()
      if (res.code === 200) {
        setUserInfo(res.data)
        return true
      } else {
        ElMessage.error(res.message || '获取用户信息失败')
        return false
      }
    } catch (error) {
      ElMessage.error('获取用户信息失败')
      return false
    }
  }

  const handleLogout = async () => {
    try {
      await logout()
    } finally {
      clearToken()
      clearUserInfo()
      ElMessage.success('已退出登录')
      router.push('/login')
    }
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    setToken,
    setUserInfo,
    clearToken,
    clearUserInfo,
    handleLogin,
    fetchUserInfo,
    handleLogout
  }
})
