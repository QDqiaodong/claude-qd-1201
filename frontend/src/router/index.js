import { createRouter, createWebHashHistory } from 'vue-router'

// 注意：main.js 里没有 app.use(router)，路由实例由 App.vue 直接驱动，
// 所以这里全部用静态 import —— <component :is> 拿到的是组件对象本身。
import LeatherView from '../views/LeatherView.vue'
import CareOrderView from '../views/CareOrderView.vue'
import PartView from '../views/PartView.vue'
import MemberView from '../views/MemberView.vue'

const routes = [
  { path: '/', redirect: '/leather' },
  { path: '/leather', name: 'leather', component: LeatherView },
  { path: '/care-order', name: 'care-order', component: CareOrderView },
  { path: '/part', name: 'part', component: PartView },
  { path: '/member', name: 'member', component: MemberView },
  { path: '/:pathMatch(.*)*', redirect: '/leather' }
]

export default createRouter({
  history: createWebHashHistory(),
  routes
})
