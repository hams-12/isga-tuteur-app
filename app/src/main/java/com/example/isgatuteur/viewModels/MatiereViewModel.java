package com.example.isgatuteur.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.isgatuteur.models.Matiere;
import com.example.isgatuteur.repositories.MatiereRepositoryImpl;

import java.util.List;

public class MatiereViewModel extends ViewModel {
    private final MatiereRepositoryImpl matiereRepository;

    public MatiereViewModel(MatiereRepositoryImpl matiereRepository){
        this.matiereRepository = matiereRepository;
    }

    public LiveData<List<Matiere>> getAllMatiereLiveData(){
        return matiereRepository.getAllMatiereLiveData();
    }

    public LiveData<List<String>> getAllDomaineLiveData(){
        return matiereRepository.getAllDomaineLiveData();
    }

    public LiveData<List<Matiere>> getMatiereByDomaineLiveData(String domaine){
        return matiereRepository.getMatiereByDomaineLiveData(domaine);
    }

    public LiveData<Boolean> checkIfUserExistInMatiere(String matiereId, String userId) {
        return matiereRepository.checkIfUserExistInMatiere(matiereId,userId);
    }

}
