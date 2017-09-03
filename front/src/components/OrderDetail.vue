<template>
    <div>
        <div class="ibox-content">
            <div class="form-horizontal">
                <div class="form-group">
                    <label class="col-lg-1 control-label">客戶名稱:</label>
                    <div class="col-lg-1"><input class="form-control" :value="customerName" readonly>
                    </div>
                    <div class="col-log-1">
                        <button class="btn btn-primary" data-toggle="modal" data-target="#customerDlg" data-backdrop="static" data-keyboard="false">查詢客戶</button>
                    </div>
                    <select-customer-dlg id="customerDlg"></select-customer-dlg>

                </div>
                <div class="form-group">
                    <label class="col-lg-1 control-label">寵物名稱:</label>
                    <div class="col-lg-1"><input class="form-control" :value="order.pet.name" readonly>
                    </div>
                    <div class="btn-group" data-toggle="buttons">
                        <label class="btn btn-outline btn-primary dim" v-for="pet in customer.petList" :class="{active: order.pet.name==pet.name}" @click="order.pet=pet">
                            <input type="radio">{{ pet.name }} </label>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-lg-1 control-label">店名:</label>
                    <div class="col-lg-1"><input class="form-control" :value="storeName" readonly>
                    </div>

                    <div class="btn-group" data-toggle="buttons">
                        <label class="btn btn-outline btn-primary dim" v-for="store in storeList" :class="{active: order.storeID==store._id}" @click="order.storeID=store._id">
                            <input type="radio">{{ store.name }} </label>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-lg-1 control-label">注意事項:</label>
                    <div class="col-lg-4"><input class="form-control" v-model="order.note">
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-lg-2 control-label">服務人員:</label>
                    <div class="col-lg-7">
                        <div class="btn-group" data-toggle="buttons">
                            <label class="btn btn-outline btn-primary dim" v-for="clerk in clerkList" :class="{active: inWorkers(clerk._id) }">
                                <input type="checkbox" :value="clerk._id" v-model="order.workers">{{ clerk.name }} </label>
                        </div>
                    </div>
                </div>

                <div class="form-group">
                    <div v-if='isEmptyOrder' class="col-lg-offset-1">
                        <button class="btn btn-primary" :disabled="!readyForSubmit" @click.prevent="upsertOrder">新增
                        </button>
                    </div>
                    <div v-else class="col-lg-offset-1">
                        <button class="btn btn-primary" :disabled="!readyForSubmit" @click.prevent="upsertOrder">更新
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<style scoped>
body {
    background-color: #0000ff;
}
</style>
<script lang='ts'>
import axios from 'axios'
import moment from 'moment'
import Datepicker from 'vuejs-datepicker'
import SelectCustomerDlg from './SelectCustomerDlg.vue'
import {
    State,
    Getter,
    Action,
    Mutation,
    namespace
} from 'vuex-class'
import { Component, Inject, Model, Prop, Vue, Watch } from 'vue-property-decorator'
import IStore from './IStore'
import IUser from './IUser'

export default class OrderDetail extends Vue {
    showSelectCustomerDlg: false
    clerkList: Array<string>
    @Watch('order.storeID') onStoreIDChange(newStoreID: string) {
        console.log("storeID is changed...")
        const url = `/StoreClerk/${newStoreID}`
        axios.get(url).then((resp) => {
            const ret = resp.data
            this.clerkList.splice(0, this.clerkList.length)
            for (let clerk of ret) {
                this.clerkList.push(clerk)
            }
        }).catch((err) => alert(err))
    }

    @Getter("user") user: IUser
    @Getter("order") order: object
    @Getter("isEmptyOrder") isEmptyOrder: boolean
    @Getter("customer") customer: object
    @Getter("isEmptyCustomer") isEmptyCustomer: boolean

    readyForSubmit() {
        if (this.order.customerID === 0
            || this.order.pet === "")
            return false;
        else
            return true;
    }

    customerName() {
        if (this.isEmptyCustomer)
            return "-"
        else {
            this.order.customerID = this.customer._id
            return this.customer.name
        }
    }

    storeName() {
        for (let store of this.storeList) {
            if (store._id == this.order.storeID)
                return store.name
        }
        return "-"
    }
}

@Action('getCustomerByID') getCustomerByID: (id: string) => void
@Action('assignPet') assignPet: (payload: object) => void
@Action('assignCustomerID') assignCustomerID: (payload: object) => void

    inWorkers(id:string) {
    return this.order.workers.indexOf(id) != -1
}
queryCustomer() {
    this.showSelectCustomerDlg = true
}
prepareOrder() {
    if (!this.order.salesId)
        this.order.salesId = this.user._id;
}
upsertOrder() {
    this.prepareOrder();
    axios.post("/Order", this.order).then(
        (resp) => {
            const data = resp.data
            if (data.ok) {
                alert("成功")
                this.$router.push({ name: 'MyOrder' })
            }
            else
                alert("失敗:" + data.msg)
        }
    ).catch((err) => {
        alert(err);
    })
}
components: {
    Datepicker,
        SelectCustomerDlg
}
}
</script>
