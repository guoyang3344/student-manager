import request from '@/utils/request'

export function getCheckinPage(params) {
  return request({
    url: '/checkin/page',
    method: 'get',
    params
  })
}

export function getCheckinById(id) {
  return request({
    url: `/checkin/${id}`,
    method: 'get'
  })
}

export function getCheckinByStudent(studentId) {
  return request({
    url: `/checkin/student/${studentId}`,
    method: 'get'
  })
}

export function getActiveCheckinByStudent(studentId) {
  return request({
    url: `/checkin/active/${studentId}`,
    method: 'get'
  })
}

export function checkin(data) {
  return request({
    url: '/checkin',
    method: 'post',
    data
  })
}

export function checkout(id, remark) {
  return request({
    url: `/checkin/checkout/${id}`,
    method: 'put',
    data: { remark }
  })
}
