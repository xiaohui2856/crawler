package com.crawlermanage.service.ownerTask;

import com.crawlermanage.service.persistence.DynamicSpecifications;
import com.google.common.collect.Lists;
import com.module.dao.entity.ownerTask.OwnerTaskDishonesty;
import com.module.dao.entity.ownerTask.OwnerTaskSipo;
import com.module.dao.repository.ownerTask.OwnerTaskSipoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;

import java.util.List;
import java.util.Map;

/**
 *
 */
@Component("sipoOTService")
public class SipoOTService {

    @Autowired
    private OwnerTaskSipoRepository ownerTaskSipoRepository;

    /**
     * 根据条件查询专利网任务
     * @param pageNumber
     * @param pagzSize
     * @param sortType
     * @return
     */
    private PageRequest buildPageRequest(int pageNumber, int pagzSize, String sortType) {

        Sort sort = new Sort(Sort.Direction.DESC, "createTaskDate");

        return new PageRequest(pageNumber - 1, pagzSize, sort);
    }


    public Page<OwnerTaskSipo> searchSipo(Map<String, Object> paramMap, int pageNumber, int pageSize,String sortType) {

        PageRequest pageRequest = buildPageRequest(pageNumber, pageSize, sortType);

        Page<OwnerTaskSipo> resultList =  findSipo(paramMap, pageRequest);

        return resultList;
    }

    private Page<OwnerTaskSipo> findSipo(Map<String, Object> searchParams,  Pageable page){

        String loginName = "";
        String searchType = "";
        String status = "";
        List<Integer> stateList = null;
        String keyWord = "";

        if (searchParams.get("loginName") != null)
            loginName = (String) searchParams.get("loginName");
        if ( searchParams.get("searchType") != null)
            searchType = (String) searchParams.get("searchType");
        if ( searchParams.get("status") != null)
            status = (String) searchParams.get("status");
        if ( searchParams.get("stateList") != null)
            stateList = (List<Integer>) searchParams.get("stateList");
        if (searchParams.get("keyWord") != null)
            keyWord = (String) searchParams.get("keyWord");


        final String finalLoginName = loginName;
        final String finalSearchType = searchType;
        final String finalKeyWord = keyWord;
        final String finalStatus = status;
        final List<Integer> finalStateList = stateList;
        return  ownerTaskSipoRepository.findAll(new Specification<OwnerTaskSipo>() {

            public Predicate toPredicate(Root<OwnerTaskSipo> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                Path<String> loginNamePath = root.get("loginName");
                Path<String> searchTypeIdPath = root.get("searchType");
                Path<Integer> statusPath = root.get("state");
                Path<String> keyWordPath = root.get("keyWord");

                //path转化
                List<Predicate> orPredicates = Lists.newArrayList();

                if (!finalLoginName.equals("")) {
                    Predicate p1 = cb.equal(loginNamePath, finalLoginName);
                    orPredicates.add(cb.and(p1));
                }
                if (!finalSearchType.equals("")){
                    Predicate p2 = cb.equal(searchTypeIdPath, finalSearchType);
                    orPredicates.add(cb.and(p2));
                }
                if (!finalKeyWord.equals("")) {
                    Predicate p3 = cb.like(keyWordPath, "%" + finalKeyWord + "%");
                    orPredicates.add(cb.and(p3));
                }
                if (finalStateList != null && !finalStateList.isEmpty()) {
                    if (finalStatus.equals("searchStatus_feedback")) {//反馈处理
                        CriteriaBuilder.In in = cb.in(statusPath);
                        for (Integer state : finalStateList) {
                            in.value(state);
                        }
                        Predicate notIn = in.not();
                        orPredicates.add(notIn);

                    } else { //全部、成功完成
                        CriteriaBuilder.In in = cb.in(statusPath);
                        for (Integer state : finalStateList) {
                            in.value(state);
                        }
                        orPredicates.add(in);
                    }
                }

                //以下是springside3提供的方法
                Predicate o = (Predicate) DynamicSpecifications.bySearchFilter(null, OwnerTaskSipo.class).toPredicate(root, query, cb);

                Predicate p = cb.and(orPredicates.toArray(new Predicate[orPredicates.size()]));
                query.where(p,o);

                return null;
            }

        }, page);

    }
    


	   //获取反馈信息,非0,1,7,-3
	  	 public List<OwnerTaskSipo> getFeedback(String loginName){
	  		    return  ownerTaskSipoRepository.findFeedBack(loginName);   
	  	 }
	  	 
	     //查询全部专利网任务
	     public List<OwnerTaskSipo> getAllIautosTask(String loginName){

	         return ownerTaskSipoRepository.findByLoginNameOrderByCreateTaskDateDesc(loginName);
	     }

	     
	     
	     //保存新任务
	     public OwnerTaskSipo joinTask(OwnerTaskSipo ownerTaskSipo){
	     	   return ownerTaskSipoRepository.save(ownerTaskSipo);
	     }
	     
	     //计算成功任务数
	 	   public Long getTaskSuccessNum(String loginName){
	 		      
	 		      return ownerTaskSipoRepository.countBySuccess(loginName);
	 	   }  
	 	   
	 	 //通过id查询任务
	 	 public OwnerTaskSipo getTask(Long id){
	 		    return ownerTaskSipoRepository.findOne(id);
	 	 }
}
