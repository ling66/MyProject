//
//  DetailViewController.m
//  ClassicPhotos
//
//  Created by l l on 28/09/12.
//  Copyright (c) 2012 ll. All rights reserved.
//

#import "DetailViewController.h"

@implementation DetailViewController

-(id)init
{
    self = [super init];
    if(self){
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.title = @"DetailView";
    
    TestView *testView = [[TestView alloc] initWithFrame:self.view.bounds];
    testView.autoresizingMask = (UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight);
    
    [self.view addSubview:testView];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation != UIInterfaceOrientationPortraitUpsideDown);
}

@end
