import axios from 'axios'

const http = axios.create({ baseURL: '/api', timeout: 20000 })

http.interceptors.response.use(
  (response) => response,
  (error) => {
    const message = error?.response?.data?.message || error?.message || '请求失败'
    return Promise.reject(new Error(message))
  }
)

async function call(promise) {
  const response = await promise
  const body = response.data
  if (!body || body.ok === false) {
    throw new Error((body && body.message) || '请求失败')
  }
  return body.data
}

const params = (o) => ({ params: o })

export default {
  // ---------------- 皮具台账 ----------------
  listLeatherGoods: () => call(http.get('/leather-goods')),
  createLeatherGood: (d) => call(http.post('/leather-goods', d)),
  updateLeatherGood: (id, d) => call(http.put(`/leather-goods/${id}`, d)),
  transferLeatherGood: (id, status) => call(http.post(`/leather-goods/${id}/status`, null, params({ status }))),

  // ---------------- 护理工单 ----------------
  listCareOrders: (leatherId) => call(http.get('/care-orders', leatherId ? params({ leatherId }) : {})),
  createCareOrder: (d) => call(http.post('/care-orders', d)),
  updateCareOrder: (id, d) => call(http.put(`/care-orders/${id}`, d)),
  completeCareOrder: (id) => call(http.post(`/care-orders/${id}/complete`)),

  // ---------------- 配件耗材 ----------------
  listParts: () => call(http.get('/parts')),
  listPartUses: () => call(http.get('/parts/uses')),
  createPart: (d) => call(http.post('/parts', d)),
  updatePart: (id, d) => call(http.put(`/parts/${id}`, d)),
  pickupPart: (id, qty) => call(http.post(`/parts/${id}/pickup`, null, params({ qty }))),

  // ---------------- 会员与取件 ----------------
  listMembers: () => call(http.get('/members')),
  createMember: (d) => call(http.post('/members', d)),
  updateMember: (id, d) => call(http.put(`/members/${id}`, d)),
  listPickups: (memberId) => call(http.get('/pickups', memberId ? params({ memberId }) : {})),
  createPickup: (memberId, leatherId, amount) =>
    call(http.post('/pickups', null, params({ memberId, leatherId, amount })))
}
