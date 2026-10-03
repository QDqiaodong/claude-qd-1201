<script setup>
import { onMounted, ref } from 'vue'
import api from '../api'
import { ElMessage } from 'element-plus'

const list = ref([])
const dialog = ref(false)
const editing = ref(null)
const form = ref({ leatherId: '', project: '清洁保养', price: 0, startTime: '' })
const projects = ['清洁保养', '补色', '换底', '换跟', '缝线']

const leatherOptions = ref([])

async function load() {
  list.value = await api.listCareOrders()
}
async function loadLeathers() {
  leatherOptions.value = await api.listLeatherGoods()
}
function openCreate() {
  editing.value = null
  form.value = { leatherId: '', project: '清洁保养', price: 0, startTime: '' }
  dialog.value = true
}
function openEdit(r) {
  editing.value = r.id
  form.value = { leatherId: r.leatherId, project: r.project, price: r.price, startTime: '' }
  dialog.value = true
}
async function save() {
  const payload = { ...form.value, price: Number(form.value.price) }
  if (editing.value) {
    await api.updateCareOrder(editing.value, payload)
    ElMessage.success('已更新')
  } else {
    await api.createCareOrder(payload)
    ElMessage.success('已开工单')
  }
  dialog.value = false
  await load()
  emit('changed')
}
async function complete(r) {
  try {
    await api.completeCareOrder(r.id)
    ElMessage.success('工单已完成')
    await load()
    emit('changed')
  } catch (e) { ElMessage.error(e.message) }
}

const emit = defineEmits(['changed'])
onMounted(async () => { await load(); await loadLeathers() })
</script>

<template>
  <div>
    <div class="bar">
      <h2>护理工单</h2>
      <el-button type="primary" @click="openCreate">新建工单</el-button>
    </div>
    <div class="grid">
      <el-card v-for="r in list" :key="r.id" class="card" shadow="hover">
        <template #header>
          <span class="code">皮具 #{{ r.leatherId }}</span>
          <el-tag size="small" :type="r.status === '已完成' ? 'success' : 'warning'">{{ r.status }}</el-tag>
        </template>
        <div class="row"><span>项目</span><b>{{ r.project }}</b></div>
        <div class="row"><span>报价</span><b>¥{{ r.price }}</b></div>
        <div class="row"><span>开始</span><b>{{ r.startTime || '—' }}</b></div>
        <div class="row"><span>完成</span><b>{{ r.finishTime || '—' }}</b></div>
        <div class="actions">
          <el-button link type="primary" @click="openEdit(r)">编辑</el-button>
          <el-button link type="success" :disabled="r.status === '已完成'" @click="complete(r)">完成</el-button>
        </div>
      </el-card>
    </div>

    <el-dialog v-model="dialog" :title="editing ? '编辑工单' : '新建工单'">
      <el-form :model="form" label-width="80px">
        <el-form-item label="皮具">
          <el-select v-model="form.leatherId" placeholder="选择皮具">
            <el-option v-for="l in leatherOptions" :key="l.id" :label="`#${l.id} ${l.category} ${l.brand}`" :value="l.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="项目">
          <el-select v-model="form.project"><el-option v-for="p in projects" :key="p" :label="p" :value="p" /></el-select>
        </el-form-item>
        <el-form-item label="报价"><el-input-number v-model="form.price" :min="0" :step="10" /></el-form-item>
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
