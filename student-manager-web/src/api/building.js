import request from '@/utils/request'

export function getBuildingPage(params) {
  return request({
    url: '/building/page',
    method: 'get',
    params
  })
}

export function getBuildingList() {
  return request({
    url: '/building/list',
    method: 'get'
  })
}

export function getBuildingById(id) {
  return request({
    url: `/building/${id}`,
    method: 'get'
  })
}

export function saveBuilding(data) {
  return request({
    url: '/building',
    method: 'post',
    data
  })
}

export function updateBuilding(data) {
  return request({
    url: '/building',
    method: 'put',
    data
  })
}

export function deleteBuilding(id) {
  return request({
    url: `/building/${id}`,
    method: 'delete'
  })
}

export function updateBuildingStatus(id, status) {
  return request({
    url: `/building/status/${id}`,
    method: 'put',
    data: { status }
  })
}
