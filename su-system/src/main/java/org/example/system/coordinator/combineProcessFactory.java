package org.example.system.coordinator;

import org.springframework.stereotype.Component;

import java.util.*;

//@Component
public class combineProcessFactory {
    public List<List<ProcessInCoordinator>> res = new ArrayList<>();
    public List<List<ProcessInCoordinator>> allCombineList(List<ProcessInCoordinator> processInCoordinatorList){

        Set<String> allMessageSent = new HashSet<String>();
        Set<String> allMessageReceived = new HashSet<String>();
        List<ProcessInCoordinator> path = new ArrayList<>();
        DFS(path, allMessageSent, allMessageReceived,0, processInCoordinatorList);
        return res;
    }


    public void DFS(List<ProcessInCoordinator> path, Set<String> allMessageSent, Set<String> allMessageReceived, int pos, List<ProcessInCoordinator> candidates){
        Comparator<ProcessInCoordinator> nameComparator = Comparator.comparing(ProcessInCoordinator::getParticipant);
        candidates.sort(nameComparator);

        if((!path.isEmpty()) && allMessageReceived.isEmpty() && allMessageSent.isEmpty()){
            System.out.println("合理path:"+path);
            res.add(new ArrayList<>(path));
        }

        for(int i = pos; i < candidates.size(); i++){
            Set<String> tempAllMessageSent = new HashSet<>(allMessageSent);
            Set<String> tempAllMessageReceived = new HashSet<>(allMessageReceived);

            path.add(candidates.get(i));

            List<String> receivedMessages = new ArrayList<>(candidates.get(i).getMessageReceived());
            for(String temp : receivedMessages){
                if(tempAllMessageSent.contains(temp)){
                    tempAllMessageSent.remove(temp);
                }else{
                    tempAllMessageReceived.add(temp);
                }
            }

            List<String> sentMessages = new ArrayList<>(candidates.get(i).getMessageSent());
            for(String temp : sentMessages){
                if(tempAllMessageReceived.contains(temp)){
                    tempAllMessageReceived.remove(temp);
                }else{
                    tempAllMessageSent.add(temp);
                }
            }

            DFS(path, new HashSet<>(tempAllMessageSent), new HashSet<>(tempAllMessageReceived), i+1, candidates);

            path.remove(path.size()-1);

        }



    }

    public combineProcessFactory() {
    }
}
