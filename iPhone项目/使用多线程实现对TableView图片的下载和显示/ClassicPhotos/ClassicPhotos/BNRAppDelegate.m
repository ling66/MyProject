//
//  BNRAppDelegate.m
//  ClassicPhotos
//
//  Created by l l on 15/09/12.
//  Copyright (c) 2012 ll. All rights reserved.
//

#import "BNRAppDelegate.h"

@implementation BNRAppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    self.window.backgroundColor = [UIColor whiteColor];
    
    //create different views for UITabBarController
    //1. the first view
//    ListViewController *listViewController = [[ListViewController alloc] initWithStyle:UITableViewStylePlain];
    ListViewController *listViewController = [[ListViewController alloc] init];
    UINavigationController *navController = [[UINavigationController alloc] initWithRootViewController:listViewController];
    
    //2. the second view
    HypnosisViewController *hypnosisViewController = [[HypnosisViewController alloc] init];
    
    //create UITabBarController
    NSArray *viewControllers = [NSArray arrayWithObjects:navController,hypnosisViewController, nil];
    UITabBarController *tabBarController = [[UITabBarController alloc] init];
    [tabBarController setViewControllers:viewControllers];
    
//    self.window.rootViewController = navController;
    self.window.rootViewController = tabBarController;
    
    [self.window makeKeyAndVisible];
    return YES;
}

- (void)applicationWillResignActive:(UIApplication *)application
{
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application
{
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

@end
