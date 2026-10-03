<script setup>
import { onMounted, ref } from 'vue'
import api from '../api'
import { ElMessage } from 'element-plus'

const members = ref([])
const pickups = ref([])
const dialog = ref(false)
const editing = ref(null)
const form = ref({ name: '', phone: '', points: 0 })
const pickupForm = ref({ memberId: '', leatherId: '', amount: 0 })
const leatherOptions = ref([])

async function load() {
  members.value = await api.listMembers()
  pickups.value = await api.listPickups()
}
async function loadLeathers() {
  leatherOptions.value = (await api.listLeatherGoods()).filter(l => l.status === '待取')
}
function openCreate() {
  editing.value = null
  form.value = { name: '', phone: '', points: 0 }
  dialog.value = true
}
function openEdit(m) {
  editing.value = m.id
  form.value = { name: m.name, phone: m.phone, points: m.points }
  dialog.value = true
}
async function save() {
  const payload = { ...form.value, points: Number(form.value.points) }
  if (editing.value) {
    await api.updateMember(editing.value, payload)
    ElMessage.success('已更新')
  } else {
    await api.createMember(payload)
    ElMessage.success('已录入会员')
  }
  dialog.value = false
  await load()
  emit('changed')
}
function openPickup() {
  pickupForm.value = { memberId: '', leatherId: '', amount: 0 }
  loadLeathers()
  pickupDialog.value = true
}
const pickupDialog = ref(false)
async function doPickup() {
  try {
    await api.createPickup(
      Number(pickupForm.value.memberId),
      Number(pickupForm.value.leatherId),
      Number(pickupForm.value.amount)
    )
    ElMessage.success('取件成功，会员积分已累计')
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
      <h2>会员与取件</h2>
      <div>
        <el-button @click="openPickup">登记取件</el-button>
        <el-button type="primary" @click="openCreate">录入会员</el-button>
      </div>
    </div>

    <h3 class="sub">会员</h3>
    <div class="grid">
      <el-card v-for="m in members" :key="m.id" class="card" shadow="hover">
        <template #header>
          <span class="code">{{ m.name }}</span>
          <el-tag size="small" :type="m.level === '普通' ? 'info' : 'success'">{{ m.level }}</el-tag>
        </template>
        <div class="row"><span>手机</span><b>{{ m.phone || '—' }}</b></div>
        <div class="row"><span>积分</span><b>{{ m.points }}</b></div>
        <div class="actions">
          <el-button link type="primary" @click="openEdit(m)">编辑</el-button>
        </div>
      </el-card>
    </div>

    <h3 class="sub">取件记录</h3>
    <el-table :data="pickups" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="memberId" label="会员" />
      <el-table-column prop="leatherId" label="皮具" />
      <el-table-column prop="amount" label="消费金额" />
      <el-table-column prop="pickupTime" label="取件时间" />
    </el-table>

    <el-dialog v-model="dialog" :title="editing ? '编辑会员' : '录入会员'">
      <el-form :model="form" label-width="80px">
        <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="手机"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="积分"><el-input-number v-model="form.points" :min="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="pickupDialog" title="登记取件">
      <el-form :model="pickupForm" label-width="80px">
        <el-form-item label="会员">
          <el-select v-model="pickupForm.memberId" placeholder="选择会员">
            <el-option v-for="m in members" :key="m.id" :label="`${m.name}（${m.level}）`" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="皮具">
          <el-select v-model="pickupForm.leatherId" placeholder="仅待取皮具">
            <el-option v-for="l in leatherOptions" :key="l.id" :label="`#${l.id} ${l.category} ${l.brand}（${l.customerName}）`" :value="l.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="消费金额"><el-input-number v-model="pickupForm.amount" :min="0" :step="10" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pickupDialog = false">取消</el-button>
        <el-button type="primary" @click="doPickup">确认取件</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; }
.sub { margin: 18px 0 10px; font-size: 15px; color: var(--muted); }
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
