<template>
    <div>
        <div v-if="customerList.length != 0">
            <table class="table table-bordered table-condensed">
                <thead>
                <tr class='info'>
                    <th></th>
                    <th>客戶編號</th>
                    <th>姓名</th>
                    <th>生日</th>
                    <th>寵物</th>
                    <th>首次來店日期</th>
                    <th>上次來店日期</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for='(customer, idx) in customerList' :class='{success:idx==detail}'>
                    <td>
                        <button class='btn btn-info' @click="showDetail(idx)"><i class="fa fa-eye"></i>&nbsp;內容</button>
                    </td>
                    <td class='text-right'>{{customer._id}}</td>
                    <td class='text-right'>{{customer.name}}</td>
                    <td class='text-right'>{{displayTime(customer.bdate)}}</td>
                    <td class='text-right' v-html="displayPet(customer)"></td>
                    <td class='text-right'>{{displayTime(customer.firstTime)}}</td>
                    <td class='text-right'>{{displayTime(customer.lastTime)}}</td>
                </tr>
                </tbody>
            </table>
            <pagination for="customerList" :records="total" :per-page="5"
                        count-text="第{from}到第{to}筆/共{count}筆|{count} 筆|1筆"></pagination>
        </div>
        <div v-else class="alert alert-info" role="alert">沒有符合的顧客</div>
        <hr>
        <div v-if='detail>=0'>
            <customer :customerParam="customer" :isNew="false"></customer>
        </div>
    </div>
</template>
<style>
</style>
<script>
    import moment from 'moment'
    import axios from 'axios'
    import {Pagination, PaginationEvent} from 'vue-pagination-2'
    import Customer from './Customer.vue'

    export default {
        props: {
            url: {
                type: String,
                required: true
            },
            param: {
                type: [Object, Array]
            }
        },
        data() {
            return {
                customerList: [],
                skip: 0,
                limit: 5,
                total: 0,
                detail: -1,
                customer: {}
            }
        },
        mounted: function () {
            this.fetchCustomer(this.skip, this.limit)
            PaginationEvent.$on('vue-pagination::customerList', this.handlePageChange)
        },
        watch: {
            url: function () {
                this.fetchCustomer(this.skip, this.limit)
            },
            param: function () {
                this.fetchCustomer(this.skip, this.limit)
            }
        },

        methods: {
            processResp(resp) {
                const ret = resp.data
                this.customerList.splice(0, this.customerList.length)
                for (let customer of ret) {
                    this.customerList.push(customer)
                }
            },
            fetchCustomer(skip, limit) {
                let request_url = `${this.url}/${skip}/${limit}`

                if (this.param) {
                    axios.post(request_url, this.param).then(this.processResp).catch((err) => {
                        alert(err)
                    })
                } else {
                    axios.get(request_url).then(this.processResp).catch((err) => {
                        alert(err)
                    })
                }
                this.fetchCustomerCount()
            },
            fetchCustomerCount() {
                let request_url = `${this.url}/count`
                if (this.param) {
                    axios.post(request_url, this.param).then(resp => {
                        this.total = resp.data
                    }).catch((err) => {
                        alert(err)
                    })
                } else {
                    axios.get(request_url).then(resp => {
                        this.total = resp.data
                    }).catch((err) => {
                        alert(err)
                    })
                }
            },
            handlePageChange(page) {
                this.skip = (page - 1) * this.limit
                this.fetchCustomer(this.skip, this.limit)
            },

            displayTime(millis) {
                return moment(millis).format('LL')
            },
            displayPet(customer) {
                const petStr =
                    customer.petList.map(pet => {
                        const info = `${pet.name} - ${pet.breed}`
                        return info
                    }).join('<br/>')
                return petStr
            },
            displayBdate(customer){
                if(customer.bdate)
                    return moment(customer.bdate).format('LL')
                else
                    return "-"
            },
            showDetail(idx) {
                this.customer = this.customerList[idx]
                this.detail = idx
                console.log(this.customer)
                console.log(this.detail)
            }
        },
        components: {
            Customer,
            Pagination
        }
    }
</script>
