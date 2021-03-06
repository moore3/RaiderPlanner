package Controller;

import Model.*;

import javax.crypto.*;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * Created by bendickson on 5/4/17.
 */
public class StudyPlannerController
{
    private StudyPlanner planner;

    public StudyPlanner getPlanner()
    {
        return planner;
    }

    /**
     * Save the current StudyPlanner into a serialized file.
     *
     * @param key64    SecretKey used for encoding.
     * @param fileName name of the file.
     * @return whether saved successfully.
     */
    public boolean save(SecretKey key64, String fileName)
    {
        try
        {
            Cipher cipher = Cipher.getInstance("Blowfish");
            cipher.init(Cipher.ENCRYPT_MODE, key64);
            SealedObject sealedObject = new SealedObject(this.planner, cipher);
            CipherOutputStream cipherOutputStream = new CipherOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)), cipher);
            ObjectOutputStream outputStream = new ObjectOutputStream(cipherOutputStream);
            outputStream.writeObject(sealedObject);
            outputStream.close();
            return true;
        } catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * '
     * Checks whether a StudyProfile for this year and semester is loaded in.
     *
     * @param year     year to be checked
     * @param semester semester number to be checked
     * @return whether this StudyProfile exists in this StudyPlanner
     */
    public boolean containsStudyProfile(int year, int semester)
    {
        return planner.containsStudyProfile(year, semester);
    }

    /**
     * if valid, this method creates a new StudyProfile and returns true
     * if invalid, it returns false
     *
     * @param hubFile HubFile containing the newly loaded in profile
     * @return whether created successfully.
     */
    public boolean createStudyProfile(HubFile hubFile)
    {
        if (!this.planner.containsStudyProfile(hubFile.getYear(), hubFile.getSemester()))
        {
            // Create a profile:
            StudyProfile profile = new StudyProfile(hubFile);
            this.planner.addStudyProfile(profile);
            if (this.planner.getCurrentStudyProfile() == null)
            {
                this.planner.setCurrentStudyProfile(profile);
                profile.setCurrent(true);
            }
            // =================

            // Fill the global calendar with newly imported events:
            ArrayList<Event> cal = hubFile.getCalendarList();
            int i = -1;
            int ii = cal.size();
            while (++i < ii)
            {
                //ConsoleIO.setConsoleMessage("Adding " + cal.get(i).toString() + " to calendar", true);
                this.planner.addEventToCalendar(cal.get(i));
                profile.addEventToCalendar(cal.get(i));
            }
            // =================

            // Notify user:
            Notification not = new Notification("New study profile created!", new GregorianCalendar(),
                    "\"" + profile.getName() + "\"", profile);
            this.planner.addNotification(not);
            // =================

            return true;
        }
        return false;
    }

    /**
     * returns a list of tasks in the current StudyProfile if it exists
     * or an empty list if it doesn't
     */
    public ArrayList<Task> getCurrentTasks()
    {
        if (this.getPlanner().getCurrentStudyProfile() != null)
            return this.getPlanner().getCurrentStudyProfile().getTasks();
        else
            return new ArrayList<>();
    }

    /**
     * Checker whether the user needs to be notified about something.
     * (Deadlines etc.)
     */
    public void checkForNotifications()
    {
        // TODO notifications
        /*int hours1 = 168, hours2 = 48; // temporary values until a Settings page is present

        for (Map.Entry<ModelEntity, boolean[]> entry : this.planner.getDeadlineNotifications().entrySet())
        {
            if (entry.getKey() instanceof Assignment)
            {
                if (!entry.getValue()[0])
                {
                    GregorianCalendar temp = new GregorianCalendar();
                    temp.add(Calendar.HOUR, -hours1);
                    Date date = temp.getTime();

                    if (entry.getKey() instanceof Coursework)
                    {
                        if (date.after((((Coursework) entry.getKey()).getDeadline().getDate())))
                        {
                            Notification not = new Notification("Assignment due in a week!",
                                    new GregorianCalendar(), entry.getKey().getName(), entry.getKey());
                            MainController.getSPC().getPlanner().addNotification(not);
                            entry.getValue()[0] = true;
                        }
                    }
                    if (entry.getKey() instanceof Exam)
                    {
                        if (date.after((((Exam) entry.getKey()).getTimeSlot().getDate())))
                        {
                            Notification not = new Notification("You have an exam in a week!",
                                    new GregorianCalendar(), entry.getKey().getName(), entry.getKey());
                            MainController.getSPC().getPlanner().addNotification(not);
                            entry.getValue()[0] = true;
                        }
                    }
                } else if (!entry.getValue()[1])
                {
                    GregorianCalendar temp = new GregorianCalendar();
                    temp.add(Calendar.HOUR, -hours2);
                    Date date = temp.getTime();

                    if (entry.getKey() instanceof Coursework)
                    {
                        if (date.after((((Coursework) entry.getKey()).getDeadline().getDate())))
                        {
                            Notification not = new Notification("Assignment due in a two days!",
                                    new GregorianCalendar(), entry.getKey().getName(), entry.getKey());
                            MainController.getSPC().getPlanner().addNotification(not);
                            entry.getValue()[1] = true;
                        }
                    }
                    if (entry.getKey() instanceof Exam)
                    {
                        if (date.after((((Exam) entry.getKey()).getTimeSlot().getDate())))
                        {
                            Notification not = new Notification("You have an exam in two days!",
                                    new GregorianCalendar(), entry.getKey().getName(), entry.getKey());
                            MainController.getSPC().getPlanner().addNotification(not);
                            entry.getValue()[1] = true;
                        }
                    }
                } else this.planner.getDeadlineNotifications().remove(entry);
            }
        }*/
    }

    /**
     * Adds a new Activity to this StudyPlanner.
     *
     * @param activity Activity to be added.
     */
    public void addActivity(Activity activity)
    {
        this.planner.addActivity(activity);
    }

    /**
     * Adds a new Milestone to this StudyPlanner.
     *
     * @param milestone Milestone to be added.
     */
    public void addMilestone(Milestone milestone)
    {
        this.planner.getCurrentStudyProfile().addMilestone(milestone);
    }

    /**
     * Removes the given Milestone from this StudyPlanner
     *
     * @param milestone Milestone to be removed.
     * @return Whether the Milestone was removed successfully.
     */
    public boolean removeMilestone(Milestone milestone)
    {
        return this.planner.getCurrentStudyProfile().removeMilestone(milestone);
    }

    /**
     * Add a new QuantityType to this StudyPlanner.
     *
     * @param quantity QuantityType to be added
     * @return whether added successfully.
     */
    public boolean addQuantityType(QuantityType quantity)
    {
        if (!this.planner.getQuantityTypes().contains(quantity))
        {
            this.planner.getQuantityTypes().add(quantity);
            return true;
        }
        return false;
    }

    /**
     * Add a new TaskType to this StudyPlanner.
     *
     * @param taskType TaskType to be added
     * @return whether added successfully.
     */
    public boolean addTaskType(TaskType taskType)
    {
        if (!this.planner.getTaskTypes().contains(taskType))
        {
            this.planner.getTaskTypes().add(taskType);
            return true;
        }
        return false;
    }

    // Constructors:
    public StudyPlannerController() throws NoSuchAlgorithmException, NoSuchPaddingException
    {
        // checks if there is a existing settings file
        if (DataController.existingSettingsFile())
        {
            // import data
        } else
        {
            // create a blank one

            // create an Account
            String fullName = "";
            boolean familyNameLast = false;
            String salutation = "";
            String email = "";

            // CONSOLE INPUT  - to be replaced by javaFX
            fullName = View.ConsoleIO.getDataString("Enter Name:");
            while (!Person.validName(fullName))
            {
                fullName = View.ConsoleIO.getDataString("I'm sorry " + fullName +
                        " I'm afraid I can't do that.\nName must only contain letters and spaces.\nPlease Enter Name:");
            }
            salutation = View.ConsoleIO.getDataString("Enter salutation:");
            while (!Person.validSalutation(salutation))
            {
                salutation = View.ConsoleIO.getDataString("Salutation must only contain letters.\nPlease Enter Salutation:");
            }
            familyNameLast = View.ConsoleIO.getDataBool("Is the family name last (y/n)");

            email = View.ConsoleIO.getDataString("Enter Email Address:");
            while (!Person.validEmail(email))
            {
                email = View.ConsoleIO.getDataString("Invalid email address.\nPlease enter a valid email address:");
            }
            Person studentDetails = new Person(salutation, fullName, familyNameLast, email);
            String studentAccountNumber = "";

            View.ConsoleIO.setConsoleMessage("Hello " + studentDetails.getSalutation() + " " + studentDetails.getFamilyName());
            View.ConsoleIO.setConsoleMessage("Thank you for creating a study profile.");


            Account newAccount = new Account(studentDetails, studentAccountNumber);


            planner = new StudyPlanner(newAccount);
        }
    }

    /**
     * Constructor for testing UI
     *
     * @param newAccount
     */
    public StudyPlannerController(Account newAccount) throws NoSuchAlgorithmException, NoSuchPaddingException
    {
        planner = new StudyPlanner(newAccount);
    }

    /**
     * Used when loading from a file
     *
     * @param planner StudyPlanner to be loaded.
     */
    public StudyPlannerController(StudyPlanner planner)
    {
        this.planner = planner;

        // Process Quantity and Task types.
        if (!this.planner.getQuantityTypes().isEmpty())
            this.planner.getQuantityTypes().forEach(e -> QuantityType.create(e));

        if (!this.planner.getTaskTypes().isEmpty())
            this.planner.getTaskTypes().forEach(e -> TaskType.create(e));

        if (!this.planner.emptyVersionControlLibrary())
            this.planner.rebuildVersionControlLibrary();
    }
}
