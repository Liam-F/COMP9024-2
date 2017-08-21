/*************************************
 *  
 *  COMP9024
 *  Assignment 3
 *  Programmed by Chunnan Sheng
 *  Student Code: 5100764
 *  
 *************************************/

package net.datastructures;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * 
 * The Task Scheduler class
 * @author Chunnan Sheng
 *
 */
public class TaskScheduler
{
    public class Task
    {
        private String m_name;
        // Execution time
        private int m_exe_time;
        // Release time
        private int m_rel_time;
        // Deadline
        private int m_deadline;

        // The latest possible start time of this task
        // If the real start time is later than this time
        // the schedule will fail
        private int m_has_to_start_time;

        // Real start time of this task
        private int m_start_time;
        
        // The CPU where the task will run
        private int m_cpu;

        /**
         * Constructor of the Task 
         * @param name
         * @param exe_time
         * @param rel_time
         * @param deadline
         */
        public Task(String name, int exe_time, int rel_time, int deadline)
        {
            this.m_name = name;
            this.m_exe_time = exe_time;
            this.m_rel_time = rel_time;
            this.m_deadline = deadline;

            this.m_has_to_start_time = deadline - exe_time;

            this.m_start_time = 0;
            this.m_cpu = 0;
        }

        public String getName()
        {
            return this.m_name;
        }

        public int getExecutionTime()
        {
            return this.m_exe_time;
        }

        public int getReleaseTime()
        {
            return this.m_rel_time;
        }

        public int getDeadline()
        {
            return this.m_deadline;
        }

        public int getHasToStartTime()
        {
            return this.m_has_to_start_time;
        }

        public int getStartTime()
        {
            return this.m_start_time;
        }

        public void setStartTime(int time)
        {
            this.m_start_time = time;
        }

        public void setCPU(int cpu)
        {
            this.m_cpu = cpu;
        }

        public int getCPU()
        {
            return this.m_cpu;
        }
    }

    /**
     * Class definition of a CPU core.
     * Information of executed tasks will be stored in this class.
     * @author Chunnan Sheng
     *
     */
    public class CPU
    {
        // ID of this CPU core
        private int m_index;
        // This is where executed tasks are stored
        private ArrayList<Task> m_tasks;
        // This is the time when this CPU becomes idle.
        // This time will be updated when a new task is added
        private int m_finish_time;

        public CPU(int index)
        {
            this.m_tasks = new ArrayList<Task>();
            this.m_index = index;
            this.m_finish_time = 0;
        }

        public int getFinishTime()
        {
            return this.m_finish_time;
        }

        /**
         * Add a new task to this CPU core.
         * This procedure may fail if the new task's deadline is smaller than
         * actual finishing time of this this task 
         * @param task
         * @return
         */
        public boolean addTask(Task task)
        {
            int start_time;
            
            if (this.m_finish_time < task.getReleaseTime())
            {
                start_time = task.getReleaseTime();
            }
            else
            {
                start_time = this.m_finish_time;
            }

            task.m_start_time = start_time;
            task.setCPU(this.m_index);
            this.m_tasks.add(task);
            this.m_finish_time = start_time + task.getExecutionTime();

            // Exactly the same as
            // if (start_time > task.getHasToStartTime())
            if (this.m_finish_time > task.getDeadline())
            {
                return false;
            }

            return true;
        }

        public ArrayList<Task> getTasks()
        {
            return this.m_tasks;
        }
    }

    /**
     * Read all tasks from file1, and insert them into a priority queue.
     * This priority queue sorts tasks via release time as key.
     * @param file1
     * @param task_heap
     * @return
     */
    private boolean readAllTasks(String file1, HeapPriorityQueue<Integer, Task> task_heap)
    {
        BufferedReader br = null;
        try
        {
            // Read all the text from the file
            br = new BufferedReader(new FileReader(file1));
            String line = null;
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null)
            {
                sb.append(line);
                sb.append(" ");
            }
            
            // Decode the text into Tasks with properties
            
            String all_info = sb.toString();
            String[] strs = all_info.split("[ \t]+");

            try
            {
                for (int i = 0; i + 3 < strs.length; i += 4)
                {
                    String name = strs[i];
                    int exe_time = Integer.parseInt(strs[i + 1]);
                    int rel_time = Integer.parseInt(strs[i + 2]);
                    int deadline = Integer.parseInt(strs[i + 3]);

                    Task new_task = new Task(name, exe_time, rel_time, deadline);

                    // Insert a new task into the heap.
                    // Release time is the key.
                    task_heap.insert(new_task.getReleaseTime(), new_task);
                }
            }
            catch (NumberFormatException ex)
            {
                ex.printStackTrace();
                System.out.println("input error when reading the attribute of the task X");
                return false;
            }

            return true;
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            System.out.println("input error when reading the attribute of the task X");
            return false;
        }
        finally
        {
            if (null != br)
            {
                try
                {
                    br.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Write the schedule into a new file.
     * Format is
     * 
     * [Task Name] [CPU ID] [Start Time]\n
     * 
     * in non-decreasing order of start time.
     * 
     * A heap is used to sort these tasks via start time.
     * 
     * @param file2
     * @param task_heap
     * @param all_cpus
     */
    private void outputSchedule(String file2, HeapPriorityQueue<Integer, Task> task_heap, ArrayList<CPU> all_cpus)
    {
        try
        {
            File out_file = new File(file2);
            out_file.createNewFile(); // if file already exists will do nothing
            FileOutputStream fos = new FileOutputStream(out_file, false);
            PrintWriter pw = null;

            pw = new PrintWriter(fos, true);

            for (CPU cpu : all_cpus)
            {
                ArrayList<Task> tasks = cpu.getTasks();
                for (Task task : tasks)
                {
                    task_heap.insert(task.getStartTime(), task);
                }
            }

            while (!task_heap.isEmpty())
            {
                Task task = task_heap.removeMin().getValue();
                pw.print(task.getName());
                pw.print(" ");
                pw.print(task.getCPU());
                pw.print(" ");
                pw.println(task.getStartTime());
            }

            pw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Print task to terminal for debugging needs
     * @param task
     */
    private void printTask(Task task)
    {
        System.out.println(task.getName() + "\t" + task.getReleaseTime() + "\t[" + task.getStartTime() + "\t"
                        + (task.getStartTime() + task.getExecutionTime()) + "]\t" + task.getDeadline());
    }

    /**
     * Print schedules to terminal for debugging needs
     * @param all_cpus
     */
    private void debugPrintSchedules(ArrayList<CPU> all_cpus)
    {
        System.out.println("----------------------------------------------");
        for (CPU cpu : all_cpus)
        {
            System.out.println("CPU: " + cpu.m_index);
            ArrayList<Task> tasks = cpu.getTasks();
            for (Task task : tasks)
            {
                this.printTask(task);
            }
        }

    }

    /**
     * The algorithm of scheduling.
     * Four heaps will be applied here.
     * <p>
     * cpu_heap:
     * <p>
     * This heap will sort all CPU's in chronological order of finishing time.
     * This means any CPU removed from this heap would be the first idle CPU.
     * If there were m CPUs,
     * deleting each CPU from this heap or inserting each CPU into
     * this heap will cost O(log(m)).
     * <p>
     * task_rls_heap:
     * <p>
     * The largest heap sort all the Tasks in chronological order of release time.
     * If there were n tasks,
     * deleting each task from this heap or inserting each task into
     * this heap will cost O(log(n)).
     * <p>
     * task_dd_heap:
     * <p>
     * There are two possible circumstances when tasks would be inserted into this heap:
     * <p>
     * 1. Tasks to run are not released yet when this CPU is already idle, then
     * add tasks of minimum release time into this heap.
     * <p>
     * 2. There are tasks who are already released because their release points 
     * are equal to or smaller than the CPU's idle time.
     * <p>
     * Tasks in this heap will be sort in deadline order(EDF).
     * If we assume that there are k tasks in this heap,
     * removing a task from this heap or inserting a task into this heap will cost O(log(k)).
     * <p>
     * task_exe_heap:
     * <p>
     * Tasks sort by EDF may have identical deadlines.
     * Tasks whose deadlines are the same will continue to be sort by execution time
     * in non-increasing order. This means a task whose execution time is longer than 
     * other ones will get higher priority than others.
     * If we assume that there are p tasks in this heap,
     * removing a task from this heap or inserting a task into this heap will cost O(log(p)).
     * <p>
     * In this algorithm, each task will go into task_rls_heap, task_dd_heap, and
     * task_exe_heap once, and go out of them again. Furthermore, for each task,
     * one CPU will be removed from the cpu_heap and inserted into the heap again.
     * Thus, cost of each task is O(log(n)) + O(log(k)) + O(log(p)) + O(log(m)).
     * <p>
     * Since k, p and m are all far fewer than n, cost of each task is equivalent to O(log(n)).
     * Therefore, the entire cost is equivalent to O(n * log(n)).
     * 
     * @param file1 Input of tasks
     * @param file2 Output of scheduled tasks
     * @param m
     */
    public void schedule_ReleaseTime_EDF_ExecutionTime(String file1, String file2, int m)
    {
        /**
         * References of all CPUs will be stored here
         */
        ArrayList<CPU> all_cpus = new ArrayList<>();

        /**
         * Heap of all CPUs. This heap will sort CPUs according to each CPU's
         * task finishing time
         */
        HeapPriorityQueue<Integer, CPU> cpu_heap = new HeapPriorityQueue<>();

        /**
         * Heap of all tasks This heap will sort all the tasks according to each
         * task's deadline
         */
        HeapPriorityQueue<Integer, Task> task_rls_heap = new HeapPriorityQueue<>();

        /**
         * A heap sorting tasks according "has to start time"
         */
        HeapPriorityQueue<Integer, Task> task_dd_heap = new HeapPriorityQueue<>();
        
        /**
         * A heap sorting tasks of the same deadline but different execution time values
         */
        HeapPriorityQueue<Integer, Task> task_exe_heap = new HeapPriorityQueue<>();

        // Insert all CPU into a CPU heap
        // All CPUs' default finish time is 0
        for (int i = 0; i < m; i++)
        {
            CPU cpu = (new TaskScheduler()).new CPU(i);
            all_cpus.add(cpu);
            cpu_heap.insert(cpu.getFinishTime(), cpu);
        }

        System.out.println("--------------------------------------------------");
        
        if (readAllTasks(file1, task_rls_heap))
        {
            while (!task_rls_heap.isEmpty())
            {
                // Get CPU of minimum finishing time
                CPU f_cpu = cpu_heap.min().getValue();
                
                /**
                 * Remove all tasks from the big heap whose release time values
                 * are the same as minimum, or remove all tasks whose release
                 * time are smaller than the CPU's finishing time.
                 */
                Task f_task = task_rls_heap.removeMin().getValue();
                task_dd_heap.insert(f_task.getDeadline(), f_task);

                while (!task_rls_heap.isEmpty())
                {
                    Task n_task = task_rls_heap.min().getValue();

                    // tasks of the same release time or
                    // tasks whose release time is smaller than CPU's finishing time
                    // will be removed from task_rls_heap, and
                    // added into task_dd_heap (EDF order).
                    if (n_task.getReleaseTime() == f_task.getReleaseTime()
                            || n_task.getReleaseTime() <= f_cpu.getFinishTime())
                    {
                        task_rls_heap.removeMin();
                        task_dd_heap.insert(n_task.getDeadline(), n_task);
                    }
                    else
                    {
                        break;
                    }
                }
                
                System.out.println("----next ready turn----");

                while (!task_dd_heap.isEmpty())
                {   
                    /**
                     * Remove tasks from task_dd_heap (EDF order) whose deadlines are
                     * identical, and add them into task_exe_heap.
                     * Tasks in task_exe_heap will be sort in non-increasing order of
                     * execution time ((-1) * (execution_time)).
                     */
                    Task task_1 = task_dd_heap.removeMin().getValue();   
                    task_exe_heap.insert((-1) * task_1.getExecutionTime(), task_1);
                    
                    while (!task_dd_heap.isEmpty())
                    {   
                        Task task_2 = task_dd_heap.min().getValue();
                        if (task_2.getDeadline() == task_1.getDeadline())
                        {
                            task_dd_heap.removeMin();
                            task_exe_heap.insert((-1) * task_2.getExecutionTime(), task_2);
                        }
                        else
                        {
                            break;
                        }
                    }
                    
                    while (!task_exe_heap.isEmpty())
                    {
                        // Remove task from task_exe_heap
                        Task task_3 = task_exe_heap.removeMin().getValue();
                        
                        this.printTask(task_3);
                        
                        // Get CPU of minimum finishing time
                        CPU cpu = cpu_heap.removeMin().getValue();
                        
                        // If the CPU of minimum finish time cannot
                        // satisfy the task
                        if (!cpu.addTask(task_3))
                        {
                            debugPrintSchedules(all_cpus);
                            System.out.println(
                                    "No feasible schedule exists for file " + file1 + " with " + m + " cores!");
                            return;
                        }
                        
                        cpu_heap.insert(cpu.getFinishTime(), cpu);
                    }
                }
            }

            debugPrintSchedules(all_cpus);
            outputSchedule(file2, new HeapPriorityQueue<Integer, Task>(), all_cpus);
        }
    }

    /**
     * The static call
     * @param file1
     * @param file2
     * @param m
     */
    public static void scheduler(String file1, String file2, int m)
    {
        new TaskScheduler().schedule_ReleaseTime_EDF_ExecutionTime(file1, file2, m);
    }

    public static void main(String[] args) throws Exception
    {

        TaskScheduler.scheduler("samplefile1.txt", "feasibleschedule1", 4);
        /** There is a feasible schedule on 4 cores */

        TaskScheduler.scheduler("samplefile1.txt", "feasibleschedule2", 3);
        /** There is no feasible schedule on 3 cores */

        TaskScheduler.scheduler("samplefile2.txt", "feasibleschedule3", 5);
        /** There is a feasible scheduler on 5 cores */

        TaskScheduler.scheduler("samplefile2.txt", "feasibleschedule4", 4);
        /** There is no feasible schedule on 4 cores */

        /** There is a feasible scheduler on 2 cores */
        TaskScheduler.scheduler("samplefile3.txt", "feasibleschedule5", 2);

        /** There is a feasible scheduler on 2 cores */
        TaskScheduler.scheduler("samplefile4.txt", "feasibleschedule6", 2);
    }
}
