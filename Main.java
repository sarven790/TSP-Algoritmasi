package myclass;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

public class MyClass {

    public static class City {
        public int Id;
        public int X;
        public int Y;
      
        public City(int _Id, int _X, int _Y) {
            this.Id = _Id;
            this.X = _X;
            this.Y = _Y;
        }

       
        public static Comparator<City> sortByX = new Comparator<City>() {
            public int compare(City link1, City link2) {
                return Integer.compare(link1.X, link2.X);
            }};

        public static Comparator<City> sortByY = new Comparator<City>() {
            public int compare(City link1, City link2) {
                return Integer.compare(link1.Y, link2.Y);
            }};
    }

     
        public static class TSP {

        public static class Link {
                public City c1;
                public City c2;
                public double distance;

                public Link(City _c1, City _c2) {
                    this.c1 = _c1;
                    this.c2 = _c2;
                    this.distance = Math.sqrt(Math.pow(c1.X - c2.X, 2) + Math.pow(c1.Y - c2.Y, 2));
                }

              
                public static Comparator<Link> sortByDistance = new Comparator<Link>() {
                    public int compare(Link link1, Link link2) {
                        return Double.compare(link1.distance, link2.distance);
                    }};
            }


           
            public ArrayList<Link> combinations(ArrayList<City> cities, int r) {
                int n = cities.size();
                ArrayList<int[]> combinations = new ArrayList<>();
                int[] combination = new int[r];

               
                for (int i = 0; i < r; i++) {
                    combination[i] = i;
                }

                while (combination[r - 1] < n) {
                    combinations.add(combination.clone());

                   
                    int t = r - 1;
                    while (t != 0 && combination[t] == n - r + t) {
                        t--;
                    }
                    combination[t]++;
                    for (int i = t + 1; i < r; i++) {
                        combination[i] = combination[i - 1] + 1;
                    }
                }

                ArrayList<Link> combinations_of_cities = new ArrayList<Link>();
                for (int i = 0; i < combinations.size(); i++) {
                    combination = combinations.get(i);
                    City city1 = cities.get(combination[0]);
                    City city2 = cities.get(combination[1]);
                    Link link = new Link(city1, city2);
                    combinations_of_cities.add(link);
                }

                return combinations_of_cities;
            }

            
            public ArrayList<Link> sorted_combinations(ArrayList<Link> combinations) {
                Collections.sort(combinations, Link.sortByDistance);
                return combinations;
            }

            
            public ArrayList<Link> shortest_links_first(ArrayList<City> cities) {
                return sorted_combinations(combinations(cities, 2));
            }

           
            public ArrayList<City> greedy_tsp(ArrayList<City> cities) {

                Hashtable<String, ArrayList<City>> endpoints = new Hashtable<String, ArrayList<City>>();
                for (City city : cities)
                {
                    ArrayList<City> cities_temp = new ArrayList<City>();
                    cities_temp.add(city);
                    endpoints.put("" + city.Id, cities_temp);
                }

                ArrayList<Link> sorted_links = shortest_links_first(cities);
                int i = 0;
                for (Link link : sorted_links)
                {
                    i = i + 1;
                    ArrayList<City> A = new ArrayList<City>();
                    ArrayList<City> B = new ArrayList<City>();
                    A.add(link.c1);
                    B.add(link.c2);
                    if (endpoints.containsKey("" + link.c1.Id) && endpoints.containsKey("" + link.c2.Id) && endpoints.get("" + link.c1.Id) != endpoints.get("" + link.c2.Id)) {
                        ArrayList<City> new_segment = join_endpoints(endpoints, link, i);
                        if(new_segment.size() == cities.size()) {
                            return new_segment;
                        }
                    }
                }
                return cities;
            }

           
            public ArrayList<City> improve_greedy_tsp(ArrayList<City> cities) {
                return improve_tour(greedy_tsp(cities));
            }

            
            public ArrayList<City> divide_tsp(ArrayList<City> cities, int n) {
                if (cities.size() <= n)
                    return exhaustive_tsp(cities);
                else {
                    ArrayList<ArrayList<City>> splited_cities = split_cities(cities);
                    ArrayList<City> half1 = splited_cities.get(0);
                    ArrayList<City> half2 = splited_cities.get(1);
                    ArrayList<City> divide1 = divide_tsp(half1, n);
                    ArrayList<City> divide2 = divide_tsp(half2, n);
                    ArrayList<City> joined_tours = join_tours(divide1, divide2);
                    return joined_tours;
                }
            }

           
            public ArrayList<ArrayList<City>> split_cities(ArrayList<City> cities) {
                ArrayList<City> cities_clone = (ArrayList<City>) cities.clone();
                int width = extent(cities, "x");
                int height = extent(cities, "y");

                if(width > height)
                    Collections.sort(cities_clone, City.sortByX);
                else
                    Collections.sort(cities_clone, City.sortByY);

                int middle = cities_clone.size() / 2;
                ArrayList<City> half1 = new ArrayList<City>(cities_clone.subList(0, middle));
                ArrayList<City> half2 = new ArrayList<City>(cities_clone.subList(middle, cities_clone.size()));
                ArrayList<ArrayList<City>> split_cities = new ArrayList<ArrayList<City>>();
                split_cities.add(half1);
                split_cities.add(half2);
                return split_cities;
            }


           
            public int extent(ArrayList<City> cities, String axis) {
                ArrayList<City> c = (ArrayList<City>) cities.clone();
                if (axis == "x") {
                    Collections.sort(c, City.sortByX);
                    City city_min_x  = c.get(0);
                    City city_max_x = c.get(c.size()-1);
                    return  city_max_x.X - city_min_x.X;
                }
                else {
                    Collections.sort(c, City.sortByY);
                    City city_min_y  = c.get(0);
                    City city_max_y = c.get(c.size()-1);
                    return  city_max_y.Y - city_min_y.Y;
                }
            }

            
            public ArrayList<City> exhaustive_tsp(ArrayList<City> cities) {
                ArrayList<City> c = (ArrayList<City>) cities.clone();
                ArrayList<ArrayList<City>> all_tours = alltours(c);
                return shortest_tour(all_tours);
            }

           
            public ArrayList<City> shortest_tour(ArrayList<ArrayList<City>> all_tours) {
                ArrayList<City> shortest_tour =  all_tours.get(0);
                double shortest_tour_length = getTourLength(shortest_tour);

                for(int i = 0; i < all_tours.size(); i++) {
                    ArrayList<City> current_tour = all_tours.get(i);
                    double current_tour_length = getTourLength(current_tour);
                    if(current_tour_length < shortest_tour_length) {
                        shortest_tour = current_tour;
                        shortest_tour_length = current_tour_length;
                    }
                }
                return shortest_tour;
            }

          
            public ArrayList<ArrayList<City>> alltours(ArrayList<City> cities) {
                ArrayList<City> c = (ArrayList<City>) cities.clone();
                c.remove(0);

                ArrayList<ArrayList<City>> permutations = listPermutations(c);
                ArrayList<ArrayList<City>> start = new ArrayList<ArrayList<City>>();
                for(ArrayList<City> City : permutations ) {
                    City.add(0, cities.get(0));
                    start.add(City);
                }
                return start;
            }

            
            public ArrayList<ArrayList<City>> listPermutations(ArrayList<City> list) {
                if (list.size() == 0) {
                    ArrayList<ArrayList<City>> result = new ArrayList<ArrayList<City>>();
                    result.add(new ArrayList<City>());
                    return result;
                }

                ArrayList<ArrayList<City>> returnMe = new ArrayList<ArrayList<City>>();

                City firstElement = list.remove(0);

                ArrayList<ArrayList<City>> recursiveReturn = listPermutations(list);
                for (List<City> li : recursiveReturn) {
                    for (int index = 0; index <= li.size(); index++) {
                        ArrayList<City> temp = new ArrayList<City>(li);
                        temp.add(index, firstElement);
                        returnMe.add(temp);
                    }
                }
                return returnMe;
            }

          
            public ArrayList<City> join_tours(ArrayList<City> tour1, ArrayList<City> tour2) {
                ArrayList<ArrayList<City>> segments1 = rotations(tour1);
                ArrayList<ArrayList<City>> segments2 = rotations(tour2);

                ArrayList<ArrayList<City>> joined_tours = new ArrayList<ArrayList<City>>();

                for (ArrayList<City> s1: segments1) {
                    for (ArrayList<City> s: segments2) {
                        ArrayList<ArrayList<City>> segments3 = new ArrayList<ArrayList<City>>();
                        segments3.add(s);
                        segments3.add(reverseArrayList(s, 0, 0));
                        for (ArrayList<City> s2: segments3) {
                            ArrayList<City> s3 = (ArrayList<City>) s1.clone();
                            s3.addAll(s2);
                            joined_tours.add(s3);
                        }
                    }
                }

                ArrayList<City> tt = shortest_tour(joined_tours);
                return tt;
            }

            
            public ArrayList<ArrayList<City>> rotations(ArrayList<City> sequence) {
                ArrayList<ArrayList<City>> rotations = new ArrayList<ArrayList<City>>();

                for(int i = 0; i < sequence.size(); i++) {
                    ArrayList<City> s1 = new ArrayList<City>(sequence.subList(i,sequence.size()));
                    ArrayList<City> s2 = new ArrayList<City>(sequence.subList(0, i));
                    ArrayList<City> s = new ArrayList<City>();
                    s.addAll(s1);
                    s.addAll(s2);
                    rotations.add(s);
                }

                return rotations;
            }

            
            public ArrayList<City> improve_divide_tsp(ArrayList<City> cities, int n) {
                ArrayList<City> divide = divide_tsp(cities, 6);
                return improve_tour(divide);
            }

           
            public ArrayList<City> join_endpoints(Hashtable<String, ArrayList<City>> endpoints, Link link, int i) {
                ArrayList<City> Aseg = endpoints.get("" + link.c1.Id);
                ArrayList<City> Bseg = endpoints.get("" + link.c2.Id);

                if(Aseg.get(Aseg.size()-1) != link.c1)
                    Aseg = reverseArrayList(Aseg, 0, 0);

                if(Bseg.get(0) != link.c2)
                    Bseg = reverseArrayList(Bseg, 0, 0);

                for (City c : Bseg) {
                    Aseg.add(c);
                }

                endpoints.remove("" + link.c1.Id);
                endpoints.remove("" + link.c2.Id);

                endpoints.put("" + Aseg.get(0).Id, Aseg);
                endpoints.put("" + Aseg.get(Aseg.size()-1).Id, Aseg);

                return Aseg;
            }

                      
            public ArrayList<City> nn_tsp (ArrayList<City> cities_main, City start) {
                ArrayList<City> cities = (ArrayList<City>)cities_main.clone();
                City C = (start != null) ? start : first(cities);

                ArrayList<City> tour = new ArrayList<>();
                tour.add(C);

                ArrayList<City> unvisited = cities;
                unvisited.remove(C);

                while(unvisited.size() > 0) {
                    C = nearest_neighbor(C, unvisited);
                    tour.add(C);
                    unvisited.remove(C);
                }

                return tour;
            }

            
            public City first(ArrayList<City> cities) {
                return cities.get(0);
            }

                 
            public City nearest_neighbor(City A, ArrayList<City> cities) {
                City nearest_neighbor = first(cities);
                double nearest_distance = distance(A, nearest_neighbor);

                for (City city : cities)
                {
                    double d = distance(A, city);
                    if(d < nearest_distance) {
                        nearest_distance = d;
                        nearest_neighbor = city;
                    }
                }

                return nearest_neighbor;
            }

           
            public double getTourLength(ArrayList<City> tour) {
                double length = 0;
                City previous_city = first(tour);

                for (City city : tour) {
                    if(city != previous_city) {
                        length += distance(previous_city, city);;
                        previous_city = city;
                    }
                }

                return length;
            }

      
            public ArrayList<City> rep_improve_nn_tsp(ArrayList<City> cities, int k) {
                ArrayList<City> shortest_tour =  cities;
                double shortest_tour_length = getTourLength(cities);

                for(int i = 0; i < k; i++) {
                    ArrayList<City> improved_tour = improve_tour(nn_tsp(cities, cities.get(i)));
                    double improved_tour_length = getTourLength(improved_tour);
                    if(improved_tour_length < shortest_tour_length) {
                        shortest_tour = improved_tour;
                        shortest_tour_length = improved_tour_length;
                    }
                }
                return shortest_tour;
            }

            
            public ArrayList<City> improve_tour(ArrayList<City> tour) {
                while(true) {
                    ArrayList<int[]> subsegments = subsegments(tour);
                    ArrayList<City> improved_tour = (ArrayList<City>) tour.clone();

                    for (int i = 0; i < subsegments.size(); i++) {
                        int[] subsegment = subsegments.get(i);
                        improved_tour = reverse_segment_if_improvement(improved_tour, subsegment[0], subsegment[1]);
                    }

                    HashSet tmp = new HashSet(tour);
                    if (tmp.containsAll(improved_tour)) {
                        return improved_tour;
                    }
                }
            }

          
            public ArrayList<City> reverse_segment_if_improvement(ArrayList<City> tour, int i, int j) {
                City A, B, C, D;

                if(i == 0) {
                    A = tour.get(tour.size()-1);
                }
                else {
                    A = tour.get(i-1);
                }

                B = tour.get(i);
                C = tour.get(j-1);
                D = tour.get(j % tour.size());

                if(distance(A, B) + distance(C, D) > distance(A, C) + distance(B, D)) {
                    return reverseArrayList(tour, i, j);
                }

                return tour;
            }

           
            public double distance(City A, City B) {
                return Math.sqrt(Math.pow(A.X - B.X, 2) + Math.pow(A.Y - B.Y, 2));
            }

            
            public ArrayList<int[]> subsegments(ArrayList<City> tour) {
                ArrayList<int[]> subsegments = new ArrayList<int[]>();

                for (City length : reverseArrayList(new ArrayList<City>(getCities().subList(2, getCities().size())), 0, 0)) {
                    for (City i : reverseArrayList(new ArrayList<City> (getCities().subList(0,getCities().size() - length.Id + 2 )),0 , 0)) {
                        int points[] = { i.Id-1, i.Id-1 + length.Id-1};
                        subsegments.add(points);
                    }
                }

                return subsegments;
            }

          
            public ArrayList<City> reverseArrayList(ArrayList<City> list, int start, int end)
            {
                if(end == 0)
                    end = list.size();

                ArrayList<City> alist = (ArrayList<City>)list.clone();
                ArrayList<City> templist = new ArrayList<City>();

                for (int i = start; i < end; i++) {
                    City temp = alist.get(start);
                    templist.add(temp);
                    alist.remove(temp);
                }


                for (int i = 0; i < templist.size(); i++) {
                    City temp = templist.get(i);
                    alist.add(start, temp);
                }


                return alist;

  
            }

       
            public ArrayList<City> getCities() {
                ArrayList<City> cities = new ArrayList<City>();

                cities.add(new City(cities.size()+1, 6734, 1453));
                cities.add(new City(cities.size()+1, 2233, 10));
                cities.add(new City(cities.size()+1, 5530, 1424));
                cities.add(new City(cities.size()+1, 401,  841));
                cities.add(new City(cities.size()+1, 3082, 1644));
                cities.add(new City(cities.size()+1, 7608, 4458));
                cities.add(new City(cities.size()+1, 7573, 3716));
                cities.add(new City(cities.size()+1, 7265, 1268));
                cities.add(new City(cities.size()+1, 6898, 1885));
                cities.add(new City(cities.size()+1, 1112, 2049));
                cities.add(new City(cities.size()+1, 5468, 2606));
                cities.add(new City(cities.size()+1, 5989, 2873));
                cities.add(new City(cities.size()+1, 4706, 2674));
                cities.add(new City(cities.size()+1, 4612, 2035));
                cities.add(new City(cities.size()+1, 6347, 2683));
                cities.add(new City(cities.size()+1, 6107, 669));
                cities.add(new City(cities.size()+1, 7611, 5184));
                cities.add(new City(cities.size()+1, 7462, 3590));
                cities.add(new City(cities.size()+1, 7732, 4723));
                cities.add(new City(cities.size()+1, 5900, 3561));
                cities.add(new City(cities.size()+1, 4483, 3369));
                cities.add(new City(cities.size()+1, 6101, 1110));
                cities.add(new City(cities.size()+1, 5199, 2182));
                cities.add(new City(cities.size()+1, 1633, 2809));
                cities.add(new City(cities.size()+1, 4307, 2322));
                cities.add(new City(cities.size()+1, 675,  1006));
                cities.add(new City(cities.size()+1, 7555, 4819));
                cities.add(new City(cities.size()+1, 7541, 3981));
                cities.add(new City(cities.size()+1, 3177, 756));
                cities.add(new City(cities.size()+1, 7352, 4506));
                cities.add(new City(cities.size()+1, 7545, 2801));
                cities.add(new City(cities.size()+1, 3245, 3305));
                cities.add(new City(cities.size()+1, 6426, 3173));
                cities.add(new City(cities.size()+1, 4608, 1198));
                cities.add(new City(cities.size()+1, 23,   2216));
                cities.add(new City(cities.size()+1, 7248, 3779));
                cities.add(new City(cities.size()+1, 7762, 4595));
                cities.add(new City(cities.size()+1, 7392, 2244));
                cities.add(new City(cities.size()+1, 3484, 2829));
                cities.add(new City(cities.size()+1, 6271, 2135));
                cities.add(new City(cities.size()+1, 4985, 140));
                cities.add(new City(cities.size()+1, 1916, 1569));
                cities.add(new City(cities.size()+1, 7280, 4899));
                cities.add(new City(cities.size()+1, 7509, 3239));
                cities.add(new City(cities.size()+1, 10,   2676));
                cities.add(new City(cities.size()+1, 6807, 2993));
                cities.add(new City(cities.size()+1, 5185, 3258));
                cities.add(new City(cities.size()+1, 3023, 1942));
                return cities;
            }
    }

    public static void main ( String [] arguments )
    {
       
        TSP tsp = new TSP();

       
        ArrayList<City> cities = tsp.getCities();

        
        long first = System.currentTimeMillis();
        ArrayList<City> tour_nearest_neighbour = tsp.rep_improve_nn_tsp(cities, 48);
        long time_nearest_neighbor = System.currentTimeMillis() - first;

     
        tour_nearest_neighbour.add(tour_nearest_neighbour.get(0));

        double tour_length_nearest_neighbor = tsp.getTourLength(tour_nearest_neighbour);

        

        System.out.println("----------------------------------");
        System.out.println("Nearest Neighbor Algorithm Results:");
        for (City city : tour_nearest_neighbour) {
            System.out.print(city.Id + ",");
        }
      
        System.out.println("\nTotal Distance:" + tour_length_nearest_neighbor + "\nTotal Time:" + time_nearest_neighbor + "ms");
        System.out.println("----------------------------------");
       

        first = System.currentTimeMillis();
        ArrayList<City> tour_divide = tsp.improve_divide_tsp(cities, 6);
        long time_divide = System.currentTimeMillis() - first;

        
        tour_divide.add(tour_divide.get(0));

        double tour_length_divide = tsp.getTourLength(tour_divide);


        System.out.println("Divide And Conquer Algorithm Results:");
        for (City city : tour_divide) {
            System.out.print(city.Id + ",");
        }
      
        System.out.println("\nTotal Distance:" + tour_length_divide + "\nTotal Time:" + time_divide + "ms");
        System.out.println("----------------------------------");
    

        first = System.currentTimeMillis();
        ArrayList<City> tour_greedy = tsp.improve_greedy_tsp(cities);
        long time_greedy = System.currentTimeMillis() - first;

     
        tour_greedy.add(tour_greedy.get(0));

        double tour_length_greedy = tsp.getTourLength(tour_greedy);

        System.out.println("Greedy Algorithm Results:");
        for (City city : tour_greedy) {
            System.out.print(city.Id + ",");
        }
       
        System.out.println("\nTotal Distance:" + tour_length_greedy + "\nTotal Time:" + time_greedy + "ms");
        System.out.println("----------------------------------");
        

        if(tour_length_nearest_neighbor < tour_length_greedy && tour_length_nearest_neighbor < tour_length_divide) {
            System.out.println("The best algortihm is Nearest Neighbour Algorithm for this problem! Length:"+tour_length_nearest_neighbor+"");
        }
        else if(tour_length_greedy < tour_length_nearest_neighbor && tour_length_greedy < tour_length_divide) {
            System.out.println("The best algortihm is Greedy Algorithm for this problem! Length:"+tour_length_greedy+"");
        }
        else if(tour_length_divide < tour_length_greedy && tour_length_divide < tour_length_nearest_neighbor) {
            System.out.println("The best algortihm is Divide And Conquer Algorithm for this problem! Length:"+tour_length_divide+"");
        }

    }
}
