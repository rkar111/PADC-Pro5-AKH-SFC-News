package com.padcmyanmar.sfc.data.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.padcmyanmar.sfc.data.vo.PublicationVO;

import java.util.List;

@Dao
public interface PublicationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertPublication(PublicationVO publication);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] isertPublications(PublicationVO... publication);

    @Query("SELECT*FROM publication")
    LiveData<List<PublicationVO>> getPublication();

    @Query("DELETE FROM publication")
    void deleteAll();

}
