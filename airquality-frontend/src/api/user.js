import request from '@/api/request'

// 获取用户个人信息
export function getProfile() {
  return request({
    url: '/user/profile',
    method: 'get'
  })
}

// 更新个人信息
export function updateProfile(data) {
  return request({
    url: '/user/profile',
    method: 'put',
    data
  })
}

// 修改密码
export function updatePassword(data) {
  return request({
    url: '/user/password',
    method: 'put',
    data
  })
}

// 获取用户统计数据
export function getUserStats() {
  return request({
    url: '/user/stats',
    method: 'get'
  })
}

// 注销账号
export function deleteAccount(data) {
  return request({
    url: '/user/account',
    method: 'delete',
    data
  })
}

// 更新头像
export function updateAvatar(data) {
  return request({
    url: '/user/avatar',
    method: 'put',
    data
  })
}

// 上传头像文件
export function uploadAvatar(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/upload/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
