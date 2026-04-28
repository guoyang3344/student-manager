import request from '@/utils/request'

export function getRepairPage(params) {
  return request({
    url: '/repair/page',
    method: 'get',
    params
  })
}

export function getRepairById(id) {
  return request({
    url: `/repair/${id}`,
    method: 'get'
  })
}

export function getRepairByStudent(studentId) {
  return request({
    url: `/repair/student/${studentId}`,
    method: 'get'
  })
}

export function createRepair(data) {
  return request({
    url: '/repair',
    method: 'post',
    data
  })
}

export function handleRepair(id, data) {
  return request({
    url: `/repair/handle/${id}`,
    method: 'put',
    data
  })
}

export function updateRepairStatus(id, data) {
  return request({
    url: `/repair/status/${id}`,
    method: 'put',
    data
  })
}
