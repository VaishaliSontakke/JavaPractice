class Solution {
    public int[] twoSum(int[] nums, int target) {
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        int[] result = {};
        for(int i=0; i < nums.length; i++) {
            int second = target - nums[i];
            if (map.get(second) != null) {

                return new int[] { i, map.get(second)};
                
            } else {
                map.put(nums[i], i);
            }

        }
        return new int[]{};
    }   

   public static void main(String[] args) {
        int[] result = twoSum(new int[] {1,2,3,4}, 7);
        if (result.length > 0)
        System.out.println("REsult is " + result[0] + " " + result[1]);


    }

}
