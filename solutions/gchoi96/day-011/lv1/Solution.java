import java.util.*;

class Solution {
    public int[] solution(String[] soccer, String[] band) {
        Set<String> set = new HashSet<>();
        Set<String> soccerSet = new HashSet<>();
        Set<String> bandSet = new HashSet<>();
        int dupCount;
        int soccerCount;
        int bandCount;

        for(int i = 0; i < 2; i++){
            Set<String> currSet = i == 0 ? soccerSet : bandSet;
            String[] names = i == 0 ? soccer : band;
            for(String name : names){
                currSet.add(name);
                set.add(name);
            }
        }

        dupCount = soccerSet.size() + bandSet.size() - set.size();
        soccerCount = soccerSet.size() - dupCount;
        bandCount = bandSet.size() - dupCount;
        return new int[]{soccerCount, bandCount, dupCount};
    }
}
