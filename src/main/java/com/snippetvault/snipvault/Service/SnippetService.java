package com.snippetvault.snipvault.Service;

import com.snippetvault.snipvault.DTO.SnippetRequest;
import com.snippetvault.snipvault.DTO.SnippetResponse;
import com.snippetvault.snipvault.Exception.ResourceNotFound;
import com.snippetvault.snipvault.repository.SnippetRepository;


import com.snippetvault.snipvault.model.snippet;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service                            // this is a business logic layer. Create a Bean for this (same as @Componenet)
@RequiredArgsConstructor            // Create constructor for repo and injects its bean here
public class SnippetService {
    private final SnippetRepository snippetRepository;

    //getAll
    public Page<SnippetResponse> getAllSnippets(Pageable pageable) {

        return snippetRepository.findAll(pageable)
                .map(this::toResponse);

    }

    //getById
    public SnippetResponse getSnippetById(Long id) {
        snippet snippetobj=snippetRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Snippet not found with id: " + id));
        return toResponse(snippetobj);
    }

    //Create
    public SnippetResponse createSnippet(SnippetRequest request) {
        snippet snippetobj=toEntity(request);
        snippet saved=snippetRepository.save(snippetobj);
        return toResponse(saved);
    }

    // Update
    public SnippetResponse updateSnippet(Long id,SnippetRequest request) {

        snippet snippetobj=snippetRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Snippet not found with id: " + id));
        snippetobj.setDescription(request.getDescription());
        snippetobj.setCode(request.getCode());
        snippetobj.setLanguage(request.getLanguage());
        snippetobj.setTitle(request.getTitle());
        return toResponse(snippetRepository.save(snippetobj));
    }

    //delete
    public void deleteSnippet(Long id) {
        if(!snippetRepository.existsById(id)){
            throw new ResourceNotFound("Snippet not found with id: " + id);
        }
        snippetRepository.deleteById(id);
    }

    //search by language
    public List<SnippetResponse> getByLanguage(String language){
        return snippetRepository.findByLanguageIgnoreCase(language)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    //search by keyword title
    public List<SnippetResponse> getByTitle(String keyword){
        return snippetRepository.findByTitleContainingIgnoreCase(keyword)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

    }
    // search by full text all fields
    public Page<SnippetResponse> search(String keyword, Pageable pageable) {

        if(keyword==null || keyword.isEmpty()){
            throw new IllegalArgumentException("Search Keyword cannot be blank");
        }
        return snippetRepository.searchByKeyword(keyword,pageable)
                .map(this::toResponse);
    }

    // - Mappers - //

    private snippet toEntity(SnippetRequest request) {
        snippet s=new snippet();
        s.setDescription(request.getDescription());
        s.setCode(request.getCode());
        s.setLanguage(request.getLanguage());
        s.setTitle(request.getTitle());
        return s;
    }

    private SnippetResponse toResponse(snippet s) {
        SnippetResponse res=new SnippetResponse();
        res.setId(s.getId());
        res.setTitle(s.getTitle());
        res.setLanguage(s.getLanguage());
        res.setDescription(s.getDescription());
        res.setCreatedAt(s.getCreatedAt());
        res.setCode(s.getCode());
        return res;


    }



}
