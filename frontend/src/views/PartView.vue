<script setup>
import { onMounted, ref } from 'vue'
import api from '../api'
import { ElMessage } from 'element-plus'

const list = ref([])
const dialog = ref(false)
const editing = ref(null)
const form = ref({ name: '', category: '胶水', stock: 0, unit: '个', warnMin: 0 })
const categories = ['胶水', '鞋线', '鞋底', '五金', '其他']
const pickupQty = ref(1)
const pickupDialog = ref(false)
const pickupTarget = ref(null)

async function load() {
  list.value = await api.listParts()
}
function openCreate() {
  editing.value = null
  form.value = { name: '', category: '胶水', stock: 0, unit: '个', warnMin: 0 }
  dialog.value = true
}
function openEdit(r) {
  editing.value = r.id
  form.value = { name: r.name, category: r.category, stock: r.stock, unit: r.unit, warnMin: r.warnMin }
  dialog.value = true
}
async function save() {
  const payload = { ...form.value, stock: Number(form.value.stock), warnMin: Number(form.value.warnMin) }
  if (editing.value) {
    await api.updatePart(editing.value, payload)
    ElMessage.success('已更新')
  } else {
    await api.createPart(payload)
    ElMessage.success('已入库')
  }
  dialog.value = false
  await load()
  emit('changed')
}
function openPickup(r) {
  pickupTarget.value = r
  pickupQty.value = 1
  pickupDialog.value = true
}
async function doPickup() {
  try {
    await api.pickupPart(pickupTarget.value.id, pickupQty.value)
    ElMessage.success('领用成功，库存已扣减')
    pickupDialog.value = false
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
      <h2>配件耗材</h2>
      <el-button type="primary" @click="openCreate">入库配件</el-button>
    </div>
    <div class="grid">
      <el-card v-for="r in list" :key="r.id" class="card" shadow="hover">
        <template #header>
          <span class="code">{{ r.name }}</span>
          <el-tag size="small" type="info">{{ r.category }}</el-tag>
        </template>
        <div class="row"><span>库存</span>
          <b :class="{ danger: r.stock < r.warnMin }">{{ r.stock }} {{ r.unit }}</b>
        </div>
        <div class="row"><span>预警下限</span><b>{{ r.warnMin }} {{ r.unit }}</b></div>
        <div class="row" v-if="r.stock < r.warnMin"><el-tag size="small" type="danger">低于下限</el-tag></div>
        <div class="actions">
          <el-button link type="primary" @click="openEdit(r)">编辑</el-button>
          <el-button link type="success" @click="openPickup(r)">领用</el-button>
        </div>
      </el-card>
    </div>

    <el-dialog v-model="dialog" :title="editing ? '编辑配件' : '入库配件'">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="类别">
          <el-select v-model="form.category"><el-option v-for="c in categories" :key="c" :label="c" :value="c" /></el-select>
        </el-form-item>
        <el-form-item label="库存"><el-input-number v-model="form.stock" :min="0" /></el-form-item>
        <el-form-item label="单位"><el-input v-model="form.unit" /></el-form-item>
        <el-form-item label="预警下限"><el-input-number v-model="form.warnMin" :min="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="pickupDialog" title="领用配件">
      <p>配件：<b>{{ pickupTarget?.name }}</b>　当前库存：<b>{{ pickupTarget?.stock }} {{ pickupTarget?.unit }}</b></p>
      <el-form label-width="80px">
        <el-form-item label="领用数量"><el-input-number v-model="pickupQty" :min="1" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pickupDialog = false">取消</el-button>
        <el-button type="primary" @click="doPickup">确认领用</el-button>
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
.row b.danger { color: #d9534f; }
.actions {
  position: absolute; left: 0; right: 0; bottom: 0;
  display: flex; gap: 4px; padding: 8px 14px;
  background: var(--primary-wash); opacity: 0; transition: opacity .2s;
}
.card:hover .actions { opacity: 1; }
</style>
