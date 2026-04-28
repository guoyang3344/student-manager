import request from '@/utils/request'

export function getDormitoryPage(params) {
  return request({
    url: '/dormitory/page',
    method: 'get',
    params
  })
}

export function getDormitoryListByBuilding(buildingId) {
  return request({
    url: `/dormitory/list/${buildingId}`,
    method: 'get'
  })
}

export function getDormitoryAvailable(buildingId) {
  return request({
    url: '/dormitory/available',
    method: 'get',
    params: { buildingId }
  })
}

export function getDormitoryById(id) {
  return request({
    url: `/dormitory/${id}`,
    method: 'get'
  })
}

export function saveDormitory(data) {
  return request({
    url: '/dormitory',
    method: 'post',
    data
  })
}

export function updateDormitory(data) {
  return request({
    url: '/dormitory',
    method: 'put',
    data
  })
}

export function deleteDormitory(id) {
  return request({
    url: `/dormitory/${id}`,
    method: 'delete'
  })
}

export function updateDormitoryStatus(id, status) {
  return request({
    url: `/dormitory/status/${id}`,
    method: 'put',
    data: { status }
  })
}
