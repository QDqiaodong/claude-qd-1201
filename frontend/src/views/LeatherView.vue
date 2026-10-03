<script setup>
import { onMounted, ref } from 'vue'
import api from '../api'
import { ElMessage } from 'element-plus'

const list = ref([])
const dialog = ref(false)
const editing = ref(null)
const form = ref({ code: '', category: '鞋', brand: '', material: '', status: '待接', customerName: '' })
const categories = ['鞋', '包', '皮带', '箱', '其他']
const statuses = ['待接', '护理中', '待取', '已取']

const nextStatus = {
  '待接': '护理中',
  '护理中': '待取',
  '待取': '已取',
  '已取': null
}

async function load() {
  list.value = await api.listLeatherGoods()
}
function openCreate() {
  editing.value = null
  form.value = { code: '', category: '鞋', brand: '', material: '', status: '待接', customerName: '' }
  dialog.value = true
}
function openEdit(r) {
  editing.value = r.id
  form.value = { ...r }
  dialog.value = true
}
async function save() {
  if (editing.value) {
    await api.updateLeatherGood(editing.value, form.value)
    ElMessage.success('已更新')
  } else {
    await api.createLeatherGood(form.value)
    ElMessage.success('已登记')
  }
  dialog.value = false
  await load()
  emit('changed')
}
async function advance(r) {
  const to = nextStatus[r.status]
  if (!to) { ElMessage.warning('已取为终态，不可回退'); return }
  try {
    await api.transferLeatherGood(r.id, to)
    ElMessage.success(`已流转至「${to}」`)
    await load()
    emit('changed')
  } catch (e) { ElMessage.error(e.message) }
}

const emit = defineEmits(['changed'])
onMounted(load)
</script>

<template>
  <div>
    <div class="bar">
      <h2>皮具台账</h2>
      <el-button type="primary" @click="openCreate">登记皮具</el-button>
    </div>
    <div class="grid">
      <el-card v-for="r in list" :key="r.id" class="card" shadow="hover">
        <template #header>
          <span class="code">{{ r.code || '无编号' }}</span>
          <el-tag size="small" type="info">{{ r.category }}</el-tag>
        </template>
        <div class="row"><span>品牌</span><b>{{ r.brand }}</b></div>
        <div class="row"><span>材质</span><b>{{ r.material }}</b></div>
        <div class="row"><span>客户</span><b>{{ r.customerName }}</b></div>
        <div class="row"><span>状态</span>
          <el-tag size="small" :type="r.status === '已取' ? 'success' : 'warning'">{{ r.status }}</el-tag>
        </div>
        <div class="actions">
          <el-button link type="primary" @click="openEdit(r)">编辑</el-button>
          <el-button link type="success" :disabled="!nextStatus[r.status]" @click="advance(r)">
            {{ nextStatus[r.status] ? '流转·' + nextStatus[r.status] : '已终态' }}
          </el-button>
        </div>
      </el-card>
    </div>

    <el-dialog v-model="dialog" :title="editing ? '编辑皮具' : '登记皮具'">
      <el-form :model="form" label-width="80px">
        <el-form-item label="编号"><el-input v-model="form.code" placeholder="如 LG2026001" /></el-form-item>
        <el-form-item label="品类">
          <el-select v-model="form.category"><el-option v-for="c in categories" :key="c" :label="c" :value="c" /></el-select>
        </el-form-item>
        <el-form-item label="品牌"><el-input v-model="form.brand" /></el-form-item>
        <el-form-item label="材质"><el-input v-model="form.material" /></el-form-item>
        <el-form-item label="客户"><el-input v-model="form.customerName" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status"><el-option v-for="s in statuses" :key="s" :label="s" :value="s" /></el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; }
.grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 14px; }
.card { position: relative; }
.card .el-card__body { padding-bottom: 42px; }
.code { font-weight: 600; margin-right: 8px; }
.row { display: flex; justify-content: space-between; padding: 4px 0; color: var(--muted); }
.row b { color: var(--ink); font-weight: 600; }
.actions {
  position: absolute; left: 0; right: 0; bottom: 0;
  display: flex; gap: 4px; padding: 8px 14px;
  background: var(--primary-wash); opacity: 0; transition: opacity .2s;
}
.card:hover .actions { opacity: 1; }
</style>
