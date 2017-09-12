<template>
    <div>
        <div class="alert alert-info" role="alert">
            <strong class="text-danger">*</strong> 必須填寫
        </div>
        <div class="form-horizontal">
            <div class="form-group">
                <label class="col-lg-1 control-label">
                    <strong class="text-danger">*</strong>姓名:</label>
                <div class="col-lg-5">
                    <input type="text" placeholder="王小白" class="form-control" required v-model="customer.name">
                </div>
            </div>
            <div class="form-group">
                <label class="col-lg-1 control-label">
                    <strong class="text-danger">*</strong>行動電話</label>
                <div class="col-lg-5">
                    <input name='phone' type="tel" placeholder="電話號碼" class="form-control" required v-model="customer.phone">
                </div>
            </div>
            <div class="form-group">
                <label class="col-lg-1 control-label">生日:</label>
                <div class='col-lg-4'>
                    <datepicker v-model="bdate" language="zh" format="MM-dd"></datepicker>
                </div>
            </div>
            <div class="form-group">
                <label class="col-lg-1 control-label">地址:</label>
                <div class="col-lg-5">
                    <input type="text" placeholder="地址" class="form-control" required v-model="customer.addr">
                </div>
            </div>
            <div class="form-group">
                <label class="col-lg-1 control-label">電子信箱:</label>
                <div class="col-lg-5">
                    <input type="email" placeholder="電子信箱" class="form-control" required v-model="customer.email">
                </div>
            </div>

            <div class="form-group">
                <label class="col-lg-1 control-label">臉書帳號:</label>
                <div class="col-lg-5">
                    <input type="email" placeholder="臉書帳號" class="form-control" required v-model="customer.facebook">
                </div>
            </div>
            <div class="form-group">
                <label class="col-lg-1 control-label">Line:</label>
                <div class="col-lg-5">
                    <input type="email" placeholder="Line帳號" class="form-control" required v-model="customer.line">
                </div>
            </div>
            <div class="form-group">
                <label class="col-lg-1 control-label">附註:</label>
                <div class="col-lg-5">
                    <input type="email" placeholder="附註:" class="form-control" required v-model="customer.note">
                </div>
            </div>
            <div class="form-group">
                <label class="col-lg-1 control-label">寵物資訊:</label>
                <div class="col-lg-8">
                    <table class="table table-bordered">
                        <thead>
                            <tr>
                                <th>名字</th>
                                <th>品種</th>
                                <th>顏色</th>
                                <th>體重</th>
                                <th>生日</th>
                                <th>習性</th>
                                <th>常去獸醫院</th>
                                <th>晶片</th>
                                <th>疫苗</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="(pet, idx) in customer.petList">
                                <td>{{pet.name}}</td>
                                <td>{{pet.breed}}</td>
                                <td>{{pet.color}}</td>
                                <td>{{pet.weight}}</td>
                                <td>{{displayPetBdate(pet)}}</td>
                                <td>{{pet.habit}}</td>
                                <td>{{pet.hospital}}</td>
                                <td>{{displayPetChip(pet)}}</td>
                                <td>{{pet.vacineList}}</td>
                                <td>
                                    <button class="btn btn-danger" @click="delPet(idx)">
                                        <i class="fa fa-trash" aria-hidden="true"></i>&nbsp;刪除
                                    </button>
                                    <button class="btn btn-warning" @click="editPet(idx)" data-toggle="modal" data-target="#petModal">
                                        <i class="fa fa-pencil" aria-hidden="true"></i>&nbsp;更新
                                    </button>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#petModal" data-backdrop="static" data-keyboard="false" @click="addPet">
                    <i class="fa fa-plus" aria-hidden="true"></i>&nbsp;新增寵物
                </button>
                <pet v-if="showPetModal" id="petModal" :isNew='newPet' :index='petIndex' :petParam='petParam' @addPet='addPetHandler' @updatePet='updatePetHandler' @cancelPet="cancelPetHandler"></pet>
            </div>

            <div class="form-group" v-if='isNewCustomer'>
                <div class="col-lg-offset-2 col-lg-10">
                    <button class="btn btn-primary" :disabled="!validCustomer" @click.prevent="newCustomer">新增顧客</button>
                </div>
            </div>
            <div class="form-group" v-else>
                <div class="col-lg-offset-2 col-lg-10">
                    <button class="btn btn-primary" :disabled="!validCustomer" @click.prevent="updateCustomer">更新顧客</button>
                </div>
            </div>
        </div>
    </div>
</template>
<style>
body {
    background-color: #ff0000;
}
</style>
<script lang="ts">
import axios from 'axios'
import moment from 'moment'
import Datepicker from 'vuejs-datepicker'
import Pet from './Pet.vue'

import {
    State,
    Getter,
    Action,
    Mutation,
    namespace
} from 'vuex-class'
import { Component, Inject, Model, Prop, Vue, Watch } from 'vue-property-decorator'
import { ICustomer, IPet } from './ICustomer'

@Component({
    components: {
        Pet: Pet,
        Datepicker
    }
})
export default class Customer extends Vue {
    newPet = true
    petIndex = 0
    showPetModal = false
    @Getter('customer') customer: ICustomer
    @Getter('isNewCustomer') isNewCustomer: boolean
    get bdate() {
        if (this.customer.bdate)
            return new Date(this.customer.bdate)
        else {
            return new Date()
        }
    }
    // setter
    set bdate(newValue: Date) {
        this.customer.bdate = newValue.getTime()
    }

    petParam() {
        const emptyPet = {
            name: "",
            breed: "",
            records: []
        }
        if (this.newPet)
            return emptyPet
        else
            return this.customer.petList[this.petIndex]
    }

    validCustomer() {
        if (!this.customer.name || this.customer.name.length === 0)
            return false
        else if (!this.customer.phone || this.customer.phone.length === 0)
            return false

        return true
    }
    @Action("emptyCustomer") emptyCustomer: () => void

    newCustomer() {
        axios.post('/Customer', this.customer).then((resp) => {
            const ret = resp.data
            if (ret.ok) {
                alert("成功")
                this.emptyCustomer()
            } else
                alert("失敗:" + ret.msg)
        }).catch((err) => {
            alert(err)
        })
    }

    updateCustomer() {
        const url = `/Customer/${this.customer._id}`
        axios.put(url, this.customer).then((resp) => {
            const ret = resp.data
            if (ret.ok) {
                alert("成功")
            }
            else
                alert("失敗:" + ret.msg)
        }).catch((err) => {
            alert(err)
        })
    }

    addPet() {
        this.showPetModal = true
        this.newPet = true
    }

    delPet(idx: number) {
        this.customer.petList.splice(idx, 1)
    }

    editPet(idx: number) {
        this.showPetModal = true
        this.newPet = false
        this.petIndex = idx
    }

    displayPetRecord(pet: IPet) {
        return ""
    }

    displayPetBdate(pet: IPet) {
        if (pet.bdate) {
            const mm = moment(pet.bdate).locale("zh_tw")
            const dateStr = mm.format('YYYY-MM-DD')
            const afterStr = mm.fromNow()
            return dateStr + " (" + afterStr + ")";
        } else
            return "-"
    }

    displayPetChip(pet: IPet) {
        if (pet.chip) {
            const mm = moment(pet.chip).locale("zh_tw")
            const dateStr = mm.format('YYYY-MM-DD')
            const afterStr = mm.fromNow()
            return dateStr + " (" + afterStr + ")";
        } else
            return "-"
    }

    displayPetVacine(pet: IPet) {
        return ""
        //return pet.vacineList.map(vacine => {
        //    const expr = `${moment().milliseconds(vacine.time).fromNow()}: ${vacine.name}`
        //    return expr
        //}).mkStr('\n')
    }

    addPetHandler(payload: object) {
        this.showPetModal = false
        this.customer.petList.push(JSON.parse(JSON.stringify(payload)))
    }

    updatePetHandler(payload: {
        index: number
        pet: IPet
    }) {
        this.showPetModal = false
        const index = payload.index
        this.customer.petList.splice(index, 1, JSON.parse(JSON.stringify(payload.pet)))
    }

    cancelPetHandler() {
        this.showPetModal = false
    }
}
</script>
