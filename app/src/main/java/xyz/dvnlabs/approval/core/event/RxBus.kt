/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.core.event

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

object RxBus {

    private val publisher = PublishSubject.create<Any>()
    fun publish(event: Any) {
        publisher.onNext(event)
    }

    /**Listen should return an Observable and not the publisher
     * Using ofType we filter only events that match that class type
     *
     * [eventType] Fill this with Event Class
     *
     **/
    fun <T> listen(eventType: Class<T>): Observable<T> = publisher.ofType(eventType)

}