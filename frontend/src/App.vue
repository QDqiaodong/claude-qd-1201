<script setup>
import { computed, onMounted, ref } from 'vue'
import router from './router'
import api from './api'

import LeatherView from './views/LeatherView.vue'
import CareOrderView from './views/CareOrderView.vue'
import PartView from './views/PartView.vue'
import MemberView from './views/MemberView.vue'

// ------------------------------------------------------------------
// 导航形态：左侧分组菜单（el-menu）。4 个业务模块分入 4 个分组，
// 操作范式：卡片网格 + 悬浮操作（每个模块用卡片展示，鼠标 hover 出操作按钮）。
// ------------------------------------------------------------------

const groups = [
  {
    title: '皮具管理',
    items: [
      { path: '/leather', label: '皮具台账', comp: LeatherView }
    ]
  },
  {
    title: '护理管理',
    items: [
      { path: '/care-order', label: '护理工单', comp: CareOrderView }
    ]
  },
  {
    title: '库存管理',
    items: [
      { path: '/part', label: '配件耗材', comp: PartView }
    ]
  },
  {
    title: '会员管理',
    items: [
      { path: '/member', label: '会员与取件', comp: MemberView }
    ]
  }
]

const allItems = groups.flatMap(g => g.items)
const currentPath = ref(router.currentRoute.value.path)
const counts = ref({})

const currentView = computed(() => {
  const m = allItems.find(i => i.path === currentPath.value)
  return m ? m.comp : LeatherView
})

async function loadCounts() {
  try {
    const [l, o, p, m] = await Promise.all([
      api.listLeatherGoods(), api.listCareOrders(),
      api.listParts(), api.listMembers()
    ])
    counts.value = {
      '/leather': l.length,
      '/care-order': o.length,
      '/part': p.length,
      '/member': m.length
    }
  } catch (e) { /* 计数失败不影响主流程 */ }
}

function onSelect(index) {
  if (index !== currentPath.value) {
    router.push(index)
  }
}

router.afterEach((to) => { currentPath.value = to.path })

onMounted(async () => {
  currentPath.value = router.currentRoute.value.path
  await loadCounts()
})
</script>

<template>
  <div class="app-shell">
    <aside class="sidebar">
      <div class="brand">
        <span class="brand-mark">鞋</span>
        <div class="brand-text">
          <div class="brand-title">修鞋皮具护理坊</div>
          <div class="brand-sub">驼色主题 · 左侧分组菜单</div>
        </div>
      </div>
      <el-menu
        class="nav-menu"
        :default-active="currentPath"
        @select="onSelect"
      >
        <el-sub-menu v-for="g in groups" :key="g.title" :index="g.title">
          <template #title>{{ g.title }}</template>
          <el-menu-item
            v-for="it in g.items"
            :key="it.path"
            :index="it.path"
          >{{ it.label }}（{{ counts[it.path] ?? 0 }}）</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </aside>

    <main class="content">
      <component :is="currentView" @changed="loadCounts" />
    </main>
  </div>
</template>

<style scoped>
.app-shell { min-height: 100vh; display: flex; }

.sidebar {
  width: 220px; flex-shrink: 0;
  background: #fff; border-right: 1px solid var(--line);
  display: flex; flex-direction: column;
}
.brand {
  display: flex; align-items: center; gap: 10px;
  padding: 16px 18px; border-bottom: 1px solid var(--line);
}
.brand-mark {
  width: 36px; height: 36px; border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
  background: var(--primary); color: #fff; font-weight: 700; font-size: 18px;
}
.brand-title { font-size: 15px; font-weight: 600; }
.brand-sub { font-size: 12px; color: var(--muted); }

.nav-menu { border-right: none; flex: 1; }

.content { flex: 1; padding: 20px 24px 28px; overflow: auto; }
</style>
