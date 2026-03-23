import request from '@/utils/request'

// 获取活动列表
export const getActivityList = (params) => {
  return request({
    url: '/activity/list',
    method: 'get',
    params
  })
}

// 获取活动详情
export const getActivityDetail = (id) => {
  return request({
    url: `/activity/${id}`,
    method: 'get'
  })
}

// 创建活动
export const createActivity = (data) => {
  return request({
    url: '/activity/create',
    method: 'post',
    data
  })
}

// 报名活动
export const registerActivity = (activityId) => {
  return request({
    url: `/activity/register/${activityId}`,
    method: 'post'
  })
}

// 我的报名记录
export const getMyRegistrations = () => {
  return request({
    url: '/activity/myRegistrations',
    method: 'get'
  })
}

// 核销时长
export const confirmHours = (registrationId) => {
  return request({
    url: `/activity/confirmHours/${registrationId}`,
    method: 'post'
  })
}

// AI生成文案
export const generateDescription = (data) => {
  return request({
    url: '/activity/ai/generate',
    method: 'post',
    data
  })
}
