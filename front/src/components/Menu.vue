<template>
    <div class="sidebar-collapse">
        <ul class="nav metismenu" id="side-menu">
            <li class="nav-header">
                <div class="dropdown profile-element">
                    <a data-toggle="dropdown" class="dropdown-toggle" href="#">
                            <span class="clear"> <span class="block m-t-xs">
                                <strong class="font-bold">{{ user.company }}</strong>
                                <strong class="font-bold">{{ user.name }}</strong></span>
                                <span class="text-muted text-xs block">{{ groupName }}
                                <b class="caret"></b></span> </span> </a>
                    <ul class="dropdown-menu animated fadeInRight m-t-xs">
                        <li><a href="#">Logout</a></li>
                    </ul>
                </div>
                <div class="logo-element">
                    IN+
                </div>
            </li>
            <li v-show="companyUser">
                <a aria-expanded="true" class="has-arrow"><i class="fa fa-th-large"></i> <span
                        class="nav-label">訂單管理</span></a>
                <ul class="nav nav-second-level collapse">
                    <router-link tag="li" to="/Order/New" active-class="active"><a><i class="fa fa-th-large"></i> <span
                            class="nav-label">新增訂單</span> </a></router-link>
                    <router-link tag="li" to="/Order/Mine" active-class="active"><a><i class="fa fa-th-large"></i> <span
                            class="nav-label">我的訂單</span> </a></router-link>
                    <router-link tag="li" to="/Order/Query" active-class="active"><a><i class="fa fa-th-large"></i>
                        <span
                                class="nav-label">查詢訂單</span> </a></router-link>
                </ul>
            </li>
            <li v-show="companyUser">
                <a aria-expanded="true" class="has-arrow"><i class="fa fa-th-large"></i> <span
                        class="nav-label">客戶管理</span></a>
                <ul class="nav nav-second-level collapse">
                    <router-link tag="li"
                                 :to="{name:'QueryCustomer'}" active-class="active">
                        <a><i class="fa fa-th-large"></i>
                            <span class="nav-label">查詢客戶</span> </a></router-link>
                    <router-link tag="li"
                                 :to="{name:'AddCustomer'}" active-class="active">
                        <a><i class="fa fa-th-large"></i>
                            <span class="nav-label">新增客戶</span> </a></router-link>
                </ul>
            </li>
            <li v-show="companyOwner">
                <a aria-expanded="true" class="has-arrow"><i class="fa fa-th-large"></i> <span
                        class="nav-label">店鋪管理</span></a>
                <ul class="nav nav-second-level collapse">
                    <router-link tag="li"
                                 :to="{name:'AddStore'}" active-class="active">
                        <a><i class="fa fa-th-large"></i>
                            <span class="nav-label"></span>新增店鋪</a></router-link>

                    <router-link tag="li"
                                 :to="{name:'DelStore'}" active-class="active">
                        <a><i class="fa fa-th-large"></i>
                            <span class="nav-label"></span>刪除店鋪</a></router-link>

                    <router-link tag="li"
                                 :to="{name:'UpdateStore'}" active-class="active">
                        <a><i class="fa fa-th-large"></i>
                            <span class="nav-label"></span>更新店鋪</a></router-link>
                    <li v-for="store in storeList"><a><i class="fa fa-th-large"></i>
                        <span class="nav-label">{{store.name}}</span> </a></li>
                </ul>
            </li>
            <li v-show="companyAdmin">
                <a><i class="fa fa-th-large"></i> <span class="nav-label">人員管理</span> <span
                        class="fa arrow"></span></a>
                <ul class="nav nav-second-level collapse">
                    <router-link tag="li"
                                 :to="{name:'AddUser'}" active-class="active">
                        <a><i class="fa fa-th-large"></i>
                            <span class="nav-label"></span>新增人員</a></router-link>

                    <router-link tag="li"
                                 :to="{name:'DelUser'}" active-class="active">
                        <a><i class="fa fa-th-large"></i>
                            <span class="nav-label"></span>刪除人員</a></router-link>

                    <router-link tag="li"
                                 :to="{name:'UpdateUser'}" active-class="active">
                        <a><i class="fa fa-th-large"></i>
                            <span class="nav-label"></span>更新人員資料</a></router-link>
                </ul>
            </li>
            <li v-show="companyClerk">
                <a><i class="fa fa-th-large"></i> <span class="nav-label">個人帳戶管理</span> <span
                        class="fa arrow"></span></a>
                <ul class="nav nav-second-level collapse">
                    <router-link tag="li"
                                 :to="{name:'UpdateUser'}" active-class="active">
                        <a><i class="fa fa-th-large"></i>
                            <span class="nav-label"></span>更新帳戶資料</a></router-link>
                </ul>
            </li>

            <li v-show="sysAdmin">
                <a aria-expanded="true" class="has-arrow"><i class="fa fa-th-large"></i> <span
                        class="nav-label">系統管理</span></a>
                <ul class="nav nav-second-level collapse">
                </ul>
            </li>
        </ul>
    </div>
</template>
<style scoped>

</style>
<script>
    import {mapGetters, mapState} from 'vuex'
    import axios from 'axios'

    export default {
        data() {
            return {
                groupInfoMap: {}
            }
        },
        mounted: function () {
            axios.get('/GroupInfo').then((resp) => {
                const ret = resp.data
                for (let groupInfo of ret) {
                    this.groupInfoMap[groupInfo.id] = groupInfo.name
                }
            }).catch((err) => {
                alert(err)
            })
        },
        computed: {
            ...mapGetters(['user', 'storeList', 'companyOwner',
                'companyAdmin', 'companyClerk', 'companyUser', 'sysAdmin']),
            groupName() {
                if (this.user.groupID && this.groupInfoMap[this.user.groupID])
                    return this.groupInfoMap[this.user.groupID]
                else
                    return ""
            }
        }
    }


</script>
