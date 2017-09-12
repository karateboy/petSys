
declare module 'vue-pagination-2' {
    import Vue from 'vue'
    export class Pagination extends Vue {

    }

    export namespace PaginationEvent {
        function $on(x: string, fn: (page: number) => void): void
    }
    //declare export Pagination: any
    //import { Pagination, PaginationEvent } from 'vue-pagination-2'
}
//export as namespace vue-pagination-2;
