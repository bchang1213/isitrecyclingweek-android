/*
 * Copyright (c) 2015 Mike Putnam <mike@theputnams.net>
 * Copyright (c) 2015 Jake Kiser <jacobvkiser@gmail.com>
 *
 * Permission to use, copy, modify, and distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package theputnams.net.isitrecyclingweek.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;

import theputnams.net.isitrecyclingweek.restclients.model.CollectionEvent;

/**
 * I created this to deal with the api returning an array of events
 */
public class RecyclingLogicHandler {

    protected CollectionEvent garbageEvent;

    protected CollectionEvent recyclingEvent;

    public RecyclingLogicHandler(CollectionEvent[] collectionEvents) {
        init(collectionEvents);
    }

    public CollectionEvent getGarbageEvent() {
        return garbageEvent;
    }

    public CollectionEvent getRecyclingEvent() {
        return recyclingEvent;
    }

    public void setRecyclingEvent(CollectionEvent recyclingEvent) {
        this.recyclingEvent = recyclingEvent;
    }

    public void setGarbageEvent(CollectionEvent garbageEvent) {
        this.garbageEvent = garbageEvent;
    }

    /**
     * This is a mess but there is no getting around it because of api limitations
     * @param collectionEvents
     * @return
     */
    protected void init(CollectionEvent[] collectionEvents){
        ArrayList<CollectionEvent> sortedEvents = new ArrayList<CollectionEvent>(Arrays.asList(collectionEvents));
        Collections.sort(sortedEvents, new Comparator<CollectionEvent>() {
            @Override
            public int compare(CollectionEvent lhs, CollectionEvent rhs) {
                int dateComp = lhs.getCollectionDate().compareTo(rhs.getCollectionDate());
                if (dateComp != 0) return dateComp;
                return rhs.getCollectionType().compareToIgnoreCase(lhs.getCollectionType());
            }
        });

        if (sortedEvents.size() > 0) {
            this.garbageEvent = sortedEvents.get(0);
            if (sortedEvents.size() > 1) {
                CollectionEvent recycling = sortedEvents.get(1);
                if (recycling != null &&
                        this.garbageEvent != null &&
                        recycling.getCollectionDate().compareTo(garbageEvent.getCollectionDate()) == 0) {
                    this.recyclingEvent = recycling;
                }
            }
        }
    }

    public boolean isRecyclingWeek() {
        return recyclingEvent != null;
    }

    public Integer getPickUpDays() {
        if (this.getGarbageEvent() != null) {

            Date nextPickup = this.getGarbageEvent().getCollectionDate();

            Calendar c = new GregorianCalendar();
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            Date today = c.getTime();

            Integer days_until_next_pickup = ((int) ((nextPickup.getTime() / (24 * 60 * 60 * 1000)) - (int) (today.getTime() / (24 * 60 * 60 * 1000))));

            return days_until_next_pickup;
        } else return 0;
    }
}
