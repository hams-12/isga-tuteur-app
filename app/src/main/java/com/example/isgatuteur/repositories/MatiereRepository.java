package com.example.isgatuteur.repositories;

import androidx.lifecycle.LiveData;

import com.example.isgatuteur.models.Matiere;

import java.util.List;

public interface MatiereRepository {
    public LiveData<List<Matiere>> getAllMatiereLiveData();
    public LiveData<List<String>> getAllDomaineLiveData();
    public LiveData<List<Matiere>> getMatiereByDomaineLiveData(String domaine);
}
